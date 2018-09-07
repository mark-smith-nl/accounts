package nl.smith.account.domain;

import static nl.smith.account.enums.AbstractEnum.getEnumByName;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.constraints.NotNull;

import nl.smith.account.enums.persisted.AccountNumber;
import nl.smith.account.enums.persisted.Currency;

public class Mutation {
	private Integer id;

	@NotNull(message = "{nl.smith.accountNumber.message}")
	private AccountNumber accountNumber;

	@NotNull(message = "{nl.smith.currency.message}")
	private Currency currency;

	private Date interestDate;

	private BigDecimal balanceBefore;

	private BigDecimal balanceAfter;

	private Date transactionDate;

	private BigDecimal amount;

	private String description;

	private Integer ordernumber;

	public Mutation() {

	}

	// Used by MyBatis
	@SuppressWarnings("unused")
	private void setId(Integer id) {
		this.id = id;
	}

	public AccountNumber getAccountNumber() {
		return accountNumber;
	}

	public Currency getCurrency() {
		return currency;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public BigDecimal getBalanceBefore() {
		return balanceBefore;
	}

	public BigDecimal getBalanceAfter() {
		return balanceAfter;
	}

	public Date getInterestDate() {
		return interestDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getDescription() {
		return description;
	}

	public Integer getId() {
		return id;
	}

	public Integer getOrdernumber() {
		return ordernumber;
	}

	@Override
	public String toString() {
		return "Mutation [accountNumber=" + accountNumber + ", currency=" + currency + ", transactionDate=" + transactionDate + ", balanceBefore=" + balanceBefore
				+ ", balanceAfter=" + balanceAfter + ", interestDate=" + interestDate + ", amount=" + amount + ", description=" + description + "]";
	}

	public static class MutationBuilder {

		private static final SimpleDateFormat IMPORT_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

		private Mutation mutation;

		private MutationBuilder() {
			mutation = new Mutation();
		}

		public static StepOne create() {
			MutationBuilder mutationBuilder = new MutationBuilder();
			return mutationBuilder.new StepOne();
		}

		public class StepOne {

			public StepTwo setAccountNumber(AccountNumber accountNumber) {
				mutation.accountNumber = accountNumber;
				return new StepTwo();
			}

			public StepTwo setAccountNumber(String accountNumber) {
				return setAccountNumber(getEnumByName(AccountNumber.class, "R" + accountNumber.replaceAll("[^\\d]", "")));
			}
		}

		public class StepTwo {
			public StepThree setCurrency(Currency currency) {
				mutation.currency = currency;
				return new StepThree();
			}

			public StepThree setCurrency(String currency) {
				return setCurrency(getEnumByName(Currency.class, currency));
			}
		}

		public class StepThree {
			public StepFour setTransactionDate(Date transactionDate) {
				mutation.transactionDate = transactionDate;
				return new StepFour();
			}

			public StepFour setTransactionDate(String transactionDate) {
				try {
					return setTransactionDate(IMPORT_DATE_FORMAT.parse(transactionDate));
				} catch (ParseException e) {
					return setTransactionDate((Date) null);
				}
			}
		}

		public class StepFour {
			public StepFive setBalanceBefore(BigDecimal balanceBefore) {
				mutation.balanceBefore = balanceBefore;
				return new StepFive();
			}

			public StepFive setBalanceBefore(String balanceBefore) {
				return setBalanceBefore(new BigDecimal(balanceBefore.replace(",", ".")));
			}
		}

		public class StepFive {
			public StepSix setBalanceAfter(BigDecimal balanceAfter) {
				mutation.balanceAfter = balanceAfter;
				return new StepSix();
			}

			public StepSix setBalanceAfter(String balanceAfter) {
				return setBalanceAfter(new BigDecimal(balanceAfter.replace(",", ".")));
			}
		}

		public class StepSix {
			public StepSeven setInterestDate(Date interestDate) {
				mutation.interestDate = interestDate;
				return new StepSeven();
			}

			public StepSeven setInterestDate(String interestDate) {
				try {
					return setInterestDate(IMPORT_DATE_FORMAT.parse(interestDate));
				} catch (ParseException e) {
					return setInterestDate((Date) null);
				}
			}
		}

		public class StepSeven {
			public StepEight setAmount(BigDecimal amount) {
				mutation.amount = amount;
				return new StepEight();
			}

			public StepEight setAmount(String amount) {
				return setAmount(new BigDecimal(amount.replace(",", ".")));
			}
		}

		public class StepEight {
			public StepFinal setDescription(String description) {
				mutation.description = description;
				return new StepFinal();
			}
		}

		public class StepFinal {
			public Mutation get() {
				return mutation;
			}
		}

	}

}
