package nl.smith.account.domain;

import static nl.smith.account.enums.AbstractEnum.getEnumByName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Stack;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import nl.smith.account.enums.persisted.AccountNumber;
import nl.smith.account.validation.FieldChecks;

public class SimpleMutation {

	protected Integer id;

	private int pageNumber;

	@NotNull(groups = FieldChecks.class)
	protected BigDecimal amount;

	@NotNull(groups = FieldChecks.class)
	protected AccountNumber accountNumber;

	@PastOrPresent(groups = FieldChecks.class)
	@NotNull(groups = FieldChecks.class)
	protected LocalDate transactionDate;

	@NotNull(groups = FieldChecks.class)
	protected String description;

	@NotNull(groups = FieldChecks.class)
	protected Integer ordernumber;

	// Not persisted. */
	private SimpleMutation previousSimpleMutation;

	// Used by MyBatis
	protected SimpleMutation() {
	}

	// Used by MyBatis
	@SuppressWarnings("unused")
	protected void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public AccountNumber getAccountNumber() {
		return accountNumber;
	}

	public LocalDate getTransactionDate() {
		return transactionDate;
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

	public Optional<SimpleMutation> getPreviousSimpleMutation() {
		return Optional.ofNullable(previousSimpleMutation);
	}

	@Override
	public String toString() {
		// @formatter:off
		
		return "Mutation [" +
				"\npageNumber                      = " + pageNumber +
				"\namount                          = " + amount +
				"\naccountNumber                   = " + accountNumber + 
				"\ntransactionDate                 = " + transactionDate + 
				"\ndescription                     = " + description +
				"\nordernumber                     = " + ordernumber +
				(getPreviousSimpleMutation().isPresent() ? (
				"\namount previous mutation = " + getPreviousSimpleMutation().get().getAmount()) : "") + 
				"]";
		// @formatter:on
	}

	public static class SimpleMutationBuilder {

		private static final DateTimeFormatter IMPORT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		private SimpleMutation simpleMutation;
		private AccountNumber accountNumber;
		private int pageNumber;

		private Stack<SimpleMutation> simpleMutations = new Stack<>();

		private SimpleMutationBuilder(AccountNumber accountNumber, int pageNumber) {
			this.accountNumber = accountNumber;
			this.pageNumber = pageNumber;

			simpleMutation = new SimpleMutation();

			simpleMutation.accountNumber = accountNumber;
			simpleMutation.pageNumber = pageNumber;
		}

		public static StepSetAmount create(AccountNumber accountNumber, int pageNumber) {
			SimpleMutationBuilder mutationBuilder = new SimpleMutationBuilder(accountNumber, pageNumber);
			return mutationBuilder.new StepSetAmount();
		}

		public static StepSetAmount create(String accountNumber, int pageNumber) {
			return create(getEnumByName(AccountNumber.class, accountNumber), pageNumber);
		}

		public class StepSetAmount {
			public StepSetTransactionDate setAmount(BigDecimal amount) {
				simpleMutation.amount = amount;
				return new StepSetTransactionDate();
			}

			public StepSetTransactionDate setAmount(String amount) {
				return setAmount(new BigDecimal(amount.replace(",", ".")));
			}

			public StepSetTransactionDate setAmount(double amount) {
				return setAmount(new BigDecimal(amount));
			}

		}

		public class StepSetTransactionDate {
			public StepSetDescription setTransactionDate(LocalDate transactionDate) {
				simpleMutation.transactionDate = transactionDate;
				return new StepSetDescription();
			}

			public StepSetDescription setTransactionDate(String transactionDate) {
				return setTransactionDate(LocalDate.parse(transactionDate, IMPORT_DATE_FORMAT));
			}
		}

		public class StepSetDescription {
			public StepFinal setDescription(String description) {
				simpleMutation.description = description;
				return new StepFinal();
			}
		}

		public class StepFinal {

			public SimpleMutation getSimpleMutation() {
				finalizeMutation();
				return simpleMutation;
			}

			public Stack<SimpleMutation> getSimpleMutations() {
				finalizeMutation();
				return simpleMutations;
			}

			public StepSetAmount add() {
				finalizeMutation();

				simpleMutation = new SimpleMutation();
				simpleMutation.accountNumber = accountNumber;
				simpleMutation.pageNumber = pageNumber;

				return new StepSetAmount();
			}

			public StepFinal add(SimpleMutation simpleMutation) {
				finalizeMutation();

				SimpleMutationBuilder.this.simpleMutation = simpleMutation;

				return new StepFinal();
			}

			// Step to determine and set the ordernumber and previous mutatiom.
			private void finalizeMutation() {
				simpleMutation.ordernumber = 1;
				if (!simpleMutations.isEmpty()) {
					SimpleMutation previousSimpleMutation = simpleMutations.peek();
					simpleMutation.previousSimpleMutation = previousSimpleMutation;

					if (previousSimpleMutation.transactionDate.equals(simpleMutation.transactionDate)) {
						simpleMutation.ordernumber += previousSimpleMutation.ordernumber;
					}

				}

				simpleMutations.add(simpleMutation);
			}
		}

	}

}
