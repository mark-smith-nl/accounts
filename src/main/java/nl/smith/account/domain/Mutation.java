package nl.smith.account.domain;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Email;

import nl.smith.account.enums.Currency;
import nl.smith.account.enums.EnumHelper;

public class Mutation {
	private Integer id;

	@Pattern(message = "{nl.smith.accountNumber.message}", regexp = "\\d{9}")
	private String accountNumber;

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

	private void setId(Integer id) {
		this.id = id;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(@Pattern(message = "{nl.smith.accountNumber.message}", regexp = "\\d{9}") String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public BigDecimal getBalanceBefore() {
		return balanceBefore;
	}

	public void setBalanceBefore(BigDecimal balanceBefore) {
		this.balanceBefore = balanceBefore;
	}

	public BigDecimal getBalanceAfter() {
		return balanceAfter;
	}

	public void setBalanceAfter(BigDecimal balanceAfter) {
		this.balanceAfter = balanceAfter;
	}

	public Date getInterestDate() {
		return interestDate;
	}

	public void setInterestDate(Date interestDate) {
		this.interestDate = interestDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getId() {
		return id;
	}

	public Integer getOrdernumber() {
		return ordernumber;
	}

	@Email
	public String getEmailAddress() {
		return "aaaaaaa";
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

			public StepTwo setAccountNumber(String accountNumber) {
				mutation.accountNumber = accountNumber;
				return new StepTwo();
			}
		}

		public class StepTwo {
			public StepThree setCurrency(String currency) {
				mutation.currency = EnumHelper.getEnumByName(Currency.class, currency);
				return new StepThree();
			}
		}

		public class StepThree {
			public StepFour setTransactionDate(String transactionDate) {
				try {
					mutation.transactionDate = IMPORT_DATE_FORMAT.parse(transactionDate);
				} catch (ParseException e) {
					mutation.transactionDate = null;
				}
				return new StepFour();
			}
		}

		public class StepFour {
			public StepFive setBalanceBefore(String balanceBefore) {
				mutation.balanceBefore = new BigDecimal(balanceBefore.replace(",", "."));
				return new StepFive();
			}
		}

		public class StepFive {
			public StepSix setBalanceAfter(String balanceAfter) {
				mutation.balanceAfter = new BigDecimal(balanceAfter.replace(",", "."));
				return new StepSix();
			}
		}

		public class StepSix {
			public StepSeven setInterestDate(String interestDate) {
				try {
					mutation.interestDate = IMPORT_DATE_FORMAT.parse(interestDate);
				} catch (ParseException e) {
					mutation.interestDate = null;
				}
				return new StepSeven();
			}
		}

		public class StepSeven {
			public StepEight setAmount(String amount) {
				mutation.amount = new BigDecimal(amount.replace(",", "."));
				return new StepEight();
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
