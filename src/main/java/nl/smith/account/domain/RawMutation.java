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

public class RawMutation {

	private Integer id;

	private int pageNumber;

	@NotNull
	private BigDecimal amount;

	@NotNull
	private AccountNumber accountNumber;

	@PastOrPresent
	@NotNull
	private LocalDate transactionDate;

	@NotNull
	private String description;

	@NotNull
	private Integer ordernumber;

	// Not persisted. */
	private RawMutation previousRawMutation;

	// Used by MyBatis
	private RawMutation() {
	}

	// Used by MyBatis
	@SuppressWarnings("unused")
	private void setId(Integer id) {
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

	public Optional<RawMutation> getPreviousRawMutation() {
		return Optional.ofNullable(previousRawMutation);
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
				(getPreviousRawMutation().isPresent() ? (
				"\namount previous mutation = " + getPreviousRawMutation().get().getAmount()) : "") + 
				"]";
		// @formatter:on
	}

	public static class RawMutationBuilder {

		private static final DateTimeFormatter IMPORT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		private RawMutation rawMutation;
		private AccountNumber accountNumber;
		private int pageNumber;

		private Stack<RawMutation> rawMutations = new Stack<>();

		private RawMutationBuilder(AccountNumber accountNumber, int pageNumber) {
			this.accountNumber = accountNumber;
			this.pageNumber = pageNumber;

			rawMutation = new RawMutation();

			rawMutation.accountNumber = accountNumber;
			rawMutation.pageNumber = pageNumber;
		}

		public static StepSetAmount create(AccountNumber accountNumber, int pageNumber) {
			RawMutationBuilder mutationBuilder = new RawMutationBuilder(accountNumber, pageNumber);
			return mutationBuilder.new StepSetAmount();
		}

		public static StepSetAmount create(String accountNumber, int pageNumber) {
			return create(getEnumByName(AccountNumber.class, accountNumber), pageNumber);
		}

		public class StepSetAmount {
			public StepSetTransactionDate setAmount(BigDecimal amount) {
				rawMutation.amount = amount;
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
				rawMutation.transactionDate = transactionDate;
				return new StepSetDescription();
			}

			public StepSetDescription setTransactionDate(String transactionDate) {
				return setTransactionDate(LocalDate.parse(transactionDate, IMPORT_DATE_FORMAT));
			}
		}

		public class StepSetDescription {
			public StepFinal setDescription(String description) {
				rawMutation.description = description;
				return new StepFinal();
			}
		}

		public class StepFinal {

			public RawMutation getRawMutation() {
				finalizeMutation();
				return rawMutation;
			}

			public Stack<RawMutation> getRawMutations() {
				finalizeMutation();
				return rawMutations;
			}

			public StepSetAmount add() {
				finalizeMutation();

				rawMutation = new RawMutation();
				rawMutation.accountNumber = accountNumber;
				rawMutation.pageNumber = pageNumber;

				return new StepSetAmount();
			}

			public StepFinal add(RawMutation rawMutation) {
				finalizeMutation();

				RawMutationBuilder.this.rawMutation = rawMutation;

				return new StepFinal();
			}

			// Step to determine and set the ordernumber and previous mutatiom.
			private void finalizeMutation() {
				rawMutation.ordernumber = 1;
				if (!rawMutations.isEmpty()) {
					RawMutation previousRawMutation = rawMutations.peek();
					rawMutation.previousRawMutation = previousRawMutation;

					if (previousRawMutation.transactionDate.equals(rawMutation.transactionDate)) {
						rawMutation.ordernumber += previousRawMutation.ordernumber;
					}

				}

				rawMutations.add(rawMutation);
			}
		}

	}

}
