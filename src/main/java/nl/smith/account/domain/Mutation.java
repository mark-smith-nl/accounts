package nl.smith.account.domain;

import static nl.smith.account.enums.AbstractEnum.getEnumByName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Stack;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import nl.smith.account.annotation.ValidMutation;
import nl.smith.account.enums.persisted.AccountNumber;
import nl.smith.account.enums.persisted.Currency;
import nl.smith.account.validation.FieldChecks;

@GroupSequence({ FieldChecks.class, Mutation.class })
@ValidMutation(allowableBalanceDifference = 0.01)
@NotNull
public class Mutation extends SimpleMutation {

	@NotNull(groups = FieldChecks.class)
	private BigDecimal balanceBefore;

	@NotNull(groups = FieldChecks.class)
	private BigDecimal balanceAfter;

	@NotNull(groups = FieldChecks.class)
	private Currency currency;

	@PastOrPresent(groups = FieldChecks.class)
	@NotNull(groups = FieldChecks.class)
	private LocalDate interestDate;

	private String remark;

	// Not persisted. */
	private Mutation previousMutation;

	// Used by MyBatis
	private Mutation() {
	}

	public Currency getCurrency() {
		return currency;
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
		
		return "Mutation [" +
				"\nbalanceBefore                   = " + balanceBefore + 
				"\nbalanceAfter                    = " + balanceAfter + 
				"\namount                          = " + amount +
				"\naccountNumber                   = " + accountNumber + 
				"\ncurrency                        = " + currency + 
				"\ninterestDate                    = " + interestDate + 
				"\ntransactionDate                 = " + transactionDate + 
				"\ndescription                     = " + description +
				"\nordernumber                     = " + ordernumber +
				"\nremark                          = " + getRemarkOption().orElse("No remark")+
				(getPreviousMutation().isPresent() ? (
				"\nBalance-after previous mutation = " + getPreviousMutation().get().getBalanceAfter()) : "") + 
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

			public StepSetAmount noBalanceAfter() {
				return new StepSetAmount();
			}

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

			public Mutation getMutation() {
				finalizeMutation();
				return mutation;
			}

			public Stack<Mutation> getMutations() {
				finalizeMutation();
				return mutations;
			}

			public StepSetAmount add() {
				finalizeMutation();

				mutation = new Mutation();
				mutation.accountNumber = accountNumber;
				mutation.currency = currency;

				return new StepSetAmount();
			}

			public StepFinal add(Mutation mutation) {
				finalizeMutation();

				MutationBuilder.this.mutation = mutation;

				return new StepFinal();
			}

			// Step to determine and set the ordernumber and previous mutatiom.
			private void finalizeMutation() {
				mutation.ordernumber = 1;
				if (!mutations.isEmpty()) {
					Mutation previousMutation = mutations.peek();
					mutation.previousMutation = previousMutation;

					if (mutation.balanceBefore == null) {
						mutation.balanceBefore = previousMutation.balanceAfter;
					}

					if (previousMutation.interestDate.equals(mutation.interestDate)) {
						mutation.ordernumber += previousMutation.ordernumber;
					}

				}

				if (mutation.balanceAfter == null && mutation.balanceBefore != null && mutation.amount != null) {
					mutation.balanceAfter = mutation.balanceBefore.add(mutation.amount);
				}

				mutations.add(mutation);
			}
		}

	}

}
