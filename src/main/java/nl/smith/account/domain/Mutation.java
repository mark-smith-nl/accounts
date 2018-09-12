package nl.smith.account.domain;

import static nl.smith.account.enums.AbstractEnum.getEnumByName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;

import nl.smith.account.annotation.ValidBalanceData;
import nl.smith.account.enums.persisted.AccountNumber;
import nl.smith.account.enums.persisted.Currency;
import nl.smith.account.validation.FieldChecks;

@GroupSequence({ FieldChecks.class, Mutation.class })
@ValidBalanceData(allowableBalanceDifference = 0.01)
public class Mutation {

	private Integer id;

	@NotNull(groups = FieldChecks.class)
	private BigDecimal balanceBefore;

	@NotNull(groups = FieldChecks.class)
	private BigDecimal balanceAfter;

	@NotNull(groups = FieldChecks.class)
	private BigDecimal amount;

	@NotNull(groups = FieldChecks.class)
	private AccountNumber accountNumber;

	@NotNull(groups = FieldChecks.class)
	private Currency currency;

	@NotNull(groups = FieldChecks.class)
	private LocalDate interestDate;

	@NotNull(groups = FieldChecks.class)
	private LocalDate transactionDate;

	@NotNull(groups = FieldChecks.class)
	private String description;

	@NotNull(groups = FieldChecks.class)
	private Integer ordernumber;

	private String remark;

	// Not persisted. */
	private Mutation previousMutation;

	private Mutation() {
	}

	// Used by MyBatis
	@SuppressWarnings("unused")
	private void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public AccountNumber getAccountNumber() {
		return accountNumber;
	}

	public Currency getCurrency() {
		return currency;
	}

	public LocalDate getTransactionDate() {
		return transactionDate;
	}

	public BigDecimal getBalanceBefore() {
		return balanceBefore;
	}

	public BigDecimal getBalanceAfter() {
		return balanceAfter;
	}

	public LocalDate getInterestDate() {
		return interestDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getDescription() {
		return description;
	}

	public Integer getOrdernumber() {
		return ordernumber;
	}

	// Used by MyBatis
	@SuppressWarnings("unused")
	private String getRemark() {
		return remark;
	}

	public Optional<String> getRemarkOption() {
		return Optional.ofNullable(remark);
	}

	public Optional<Mutation> getPreviousMutation() {
		return Optional.ofNullable(previousMutation);
	}

	@Override
	public String toString() {
		// @formatter:off
		
		return "Mutation "
				+ "[accountNumber=" + accountNumber + 
				", currency=" + currency + 
				", transactionDate=" + transactionDate + 
				", balanceBefore=" + balanceBefore + 
				", balanceAfter=" + balanceAfter + 
				", interestDate=" + interestDate + 
				", amount=" + amount + 
				", description=" + description + 
				", remark=" + getRemarkOption().orElse("No remark")+
				(getPreviousMutation().isPresent() ? ("Balance-after previous mutation=" + getPreviousMutation().get().getBalanceAfter()) : "") + 
				"]";
		// @formatter:on
	}

	public static class MutationBuilder {

		private static final DateTimeFormatter IMPORT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

		private Mutation mutation;
		private AccountNumber accountNumber;
		private Currency currency;

		private Stack<Mutation> mutations = new Stack<>();

		private MutationBuilder(AccountNumber accountNumber, Currency currency) {
			this.accountNumber = accountNumber;
			this.currency = currency;

			mutation = new Mutation();

			mutation.accountNumber = accountNumber;
			mutation.currency = currency;
		}

		public static StepSetBalanceBefore create(AccountNumber accountNumber, Currency currency) {
			MutationBuilder mutationBuilder = new MutationBuilder(accountNumber, currency);
			return mutationBuilder.new StepSetBalanceBefore();
		}

		public static StepSetBalanceBefore create(String accountNumber, Currency currency) {
			return create(getEnumByName(AccountNumber.class, accountNumber), currency);
		}

		public static StepSetBalanceBefore create(AccountNumber accountNumber, String currency) {
			return create(accountNumber, getEnumByName(Currency.class, currency));
		}

		public static StepSetBalanceBefore create(String accountNumber, String currency) {
			return create(getEnumByName(AccountNumber.class, accountNumber), getEnumByName(Currency.class, currency));
		}

		public class StepSetBalanceBefore {
			public StepSetBalanceAfter setBalanceBefore(BigDecimal balanceBefore) {
				mutation.balanceBefore = balanceBefore;
				return new StepSetBalanceAfter();
			}

			public StepSetBalanceAfter setBalanceBefore(String balanceBefore) {
				return setBalanceBefore(new BigDecimal(balanceBefore.replace(",", ".")));
			}

			public StepSetBalanceAfter setBalanceBefore(double balanceBefore) {
				return setBalanceBefore(new BigDecimal(balanceBefore));
			}
		}

		public class StepSetBalanceAfter {
			public StepSetAmount setBalanceAfter(BigDecimal balanceAfter) {
				mutation.balanceAfter = balanceAfter;
				return new StepSetAmount();
			}

			public StepSetAmount setBalanceAfter(String balanceAfter) {
				return setBalanceAfter(new BigDecimal(balanceAfter.replace(",", ".")));
			}

			public StepSetAmount setBalanceAfter(double balanceAfter) {
				return setBalanceAfter(new BigDecimal(balanceAfter));
			}

		}

		public class StepSetAmount {
			public StepSetInterestAndOrTransactionDate setAmount(BigDecimal amount) {
				mutation.amount = amount;
				return new StepSetInterestAndOrTransactionDate();
			}

			public StepSetInterestAndOrTransactionDate setAmount(String amount) {
				return setAmount(new BigDecimal(amount.replace(",", ".")));
			}

			public StepSetInterestAndOrTransactionDate setAmount(double amount) {
				return setAmount(new BigDecimal(amount));
			}

		}

		public class StepSetInterestAndOrTransactionDate {
			public StepSetDescription setInterestAndTransactionDate(LocalDate interestAndTransactionDate) {
				mutation.interestDate = interestAndTransactionDate;
				mutation.transactionDate = interestAndTransactionDate;
				return new StepSetDescription();
			}

			public StepSetDescription setInterestAndTransactionDate(String interestAndTransactionDate) {
				return setInterestAndTransactionDate(LocalDate.parse(interestAndTransactionDate, IMPORT_DATE_FORMAT));
			}

			public StepSetTransactionDate setInterestDate(LocalDate interestDate) {
				mutation.interestDate = interestDate;
				return new StepSetTransactionDate();
			}

			public StepSetTransactionDate setInterestDate(String interestDate) {
				return setInterestDate(LocalDate.parse(interestDate, IMPORT_DATE_FORMAT));
			}
		}

		public class StepSetTransactionDate {
			public StepSetDescription setTransactionDate(LocalDate transactionDate) {
				mutation.transactionDate = transactionDate;
				return new StepSetDescription();
			}

			public StepSetDescription setTransactionDate(String transactionDate) {
				return setTransactionDate(LocalDate.parse(transactionDate, IMPORT_DATE_FORMAT));
			}
		}

		public class StepSetDescription {
			public StepFinal setDescription(String description) {
				mutation.description = description;
				return new StepFinal();
			}
		}

		public class StepFinal {

			public StepFinal setRemark(String remark) {
				mutation.remark = remark;
				return this;
			}

			public Mutation get() {
				finalizeMutation();
				return mutation;
			}

			public List<Mutation> getAll() {
				finalizeMutation();

				mutations.add(mutation);

				return mutations;
			}

			public StepSetBalanceAfter add() {
				finalizeMutation();

				mutations.add(mutation);

				BigDecimal balanceAfter = mutation.balanceAfter;

				mutation = new Mutation();
				mutation.accountNumber = accountNumber;
				mutation.currency = currency;
				mutation.balanceBefore = balanceAfter;

				return new StepSetBalanceAfter();
			}

			public StepFinal add(Mutation mutation) {
				// @formatter:off
				if (MutationBuilder.this.mutation.accountNumber != mutation.accountNumber || 
						MutationBuilder.this.mutation.currency != mutation.currency	|| 
						mutation.ordernumber == 0) {
					throw new IllegalArgumentException();
				}
                // @formatter:on

				finalizeMutation();

				mutations.add(mutation);

				MutationBuilder.this.mutation = mutation;

				finalizeMutation();

				return new StepFinal();
			}

			// Step to determine and set the ordernumber and previous mutatiom.
			private void finalizeMutation() {
				if (!mutations.isEmpty()) {
					Mutation previousMutation = mutations.peek();
					mutation.previousMutation = previousMutation;
					if (previousMutation.interestDate.equals(mutation.interestDate)) {
						mutation.ordernumber = previousMutation.ordernumber + 1;
					}
				} else {
					mutation.ordernumber = 1;
				}
			}
		}

	}

}
