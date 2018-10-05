package nl.smith.account.development.test;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import nl.smith.account.enums.persisted.AccountNumber;
import nl.smith.account.enums.persisted.Currency;
import nl.smith.account.validation.FieldChecks;

// Financial mutation.
public class Mutation {

	private Integer id;

	@NotNull(groups = { RawPojoFields.class }, message = "Field amount is required")
	private AccountNumber accountNumber;

	private int pageNumber;

	@NotNull(groups = { RawPojoFields.class }, message = "Field amount is required")
	private BigDecimal amount;

	@NotNull(groups = { RawPojoFields.class }, message = "Field transactionDate is required")
	@PastOrPresent(groups = { RawPojoFields.class })
	private LocalDate transactionDate;

	@NotNull(groups = { RawPojoFields.class }, message = "Field description is required")
	private String description;

	@NotNull(groups = { RawPojoFields.class }, message = "Field ordernumber is required")
	private Integer ordernumber;

	@NotNull(groups = { RawPojoFields.class }, message = "Field balanceBefore is required")
	private BigDecimal balanceBefore;

	@NotNull(groups = { RawPojoFields.class }, message = "Field balanceAfter is required")
	private BigDecimal balanceAfter;

	@NotNull(groups = { RawPojoFields.class }, message = "Field currency is required")
	private Currency currency;

	@PastOrPresent(groups = { RawPojoFields.class }, message = "Field interestDate is required")
	@NotNull(groups = FieldChecks.class)
	private LocalDate interestDate;

	private String remark;

	private Mutation previousMutation;

	// Used by MyBatis
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

	public BigDecimal getAmount() {
		return amount;
	}

	public AccountNumber getAccountNumber() {
		return accountNumber;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public LocalDate getTransactionDate() {
		return transactionDate;
	}

	public String getDescription() {
		return description;
	}

	public Integer getOrdernumber() {
		return ordernumber;
	}

	public BigDecimal getBalanceBefore() {
		return balanceBefore;
	}

	public BigDecimal getBalanceAfter() {
		return balanceAfter;
	}

	public Currency getCurrency() {
		return currency;
	}

	public LocalDate getInterestDate() {
		return interestDate;
	}

	public String getRemark() {
		return remark;
	}

	public Mutation getPreviousMutation() {
		return previousMutation;
	}

	interface CompletePojoFields {
	}

	interface RawPojoFields {
	}

	interface StepAfterStepSetDescription {
	}

	public static <T extends StepAfterStepSetDescription> MutationBuilder<T> getBuilderFor(Class<T> clazz) {
		try {
			MutationBuilder<T> pojoBuilder = new MutationBuilder<T>();
			pojoBuilder.stepAfterStepSetDescription = clazz.getConstructor(new Class[] { MutationBuilder.class }).newInstance(new Object[] { pojoBuilder });
			return pojoBuilder;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new IllegalStateException();
		}
	}

	public static class MutationBuilder<T extends StepAfterStepSetDescription> {

		private static final DateTimeFormatter IMPORT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		private Mutation mutation = new Mutation();

		private T stepAfterStepSetDescription;

		private MutationBuilder() {

		}

		public MutationBuilder(Mutation previousMutation) {
			mutation = new Mutation();
			mutation.previousMutation = previousMutation;
			mutation.balanceBefore = previousMutation.balanceAfter;
		}

		public StepSetTransactionDate setAmount(BigDecimal amount) {
			mutation.amount = amount;
			return new StepSetTransactionDate();
		}

		public StepSetTransactionDate setAmount(String amount) {
			return setAmount(new BigDecimal(amount.replace(",", ".")));
		}

		public StepSetTransactionDate setAmount(double amount) {
			return setAmount(new BigDecimal(amount));
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
			public T setDescription(String description) {
				mutation.description = description;
				return stepAfterStepSetDescription;
			}
		}

		public class CompleteMutationWithoutBalanceBeforeAndAfterSpecification extends CompleteMutation {
			public void doit() {

			}

		}

		public class CompleteMutation implements StepAfterStepSetDescription {
			public StepBalanceAfter setBalanceBeforeToBalanceAfterPreviousMutation(BigDecimal balanceBefore) {
				mutation.balanceBefore = balanceBefore;
				return new StepBalanceAfter();
			}

			public StepBalanceAfter setBalanceBefore(BigDecimal balanceBefore) {
				mutation.balanceBefore = balanceBefore;
				return new StepBalanceAfter();
			}

			public StepBalanceAfter setBalanceBefore(String balanceBefore) {
				return setBalanceBefore(new BigDecimal(balanceBefore.replace(",", ".")));
			}

			public StepBalanceAfter setBalanceBefore(double balanceBefore) {
				return setBalanceBefore(new BigDecimal(balanceBefore));
			}

		}

		public class StepBalanceAfter {
			public FullMutation setBalanceAfter(BigDecimal balanceAfter) {
				mutation.balanceAfter = balanceAfter;
				return new FullMutation();
			}

			public FullMutation setBalanceAfter(String balanceAfter) {
				return setBalanceAfter(new BigDecimal(balanceAfter.replace(",", ".")));
			}

			public FullMutation setBalanceAfter(double balanceAfter) {
				return setBalanceAfter(new BigDecimal(balanceAfter));
			}
		}

		public class StepRemark {
			public RawMutation setRemark(String remark) {
				mutation.remark = remark;
				return new FullMutation();
			}

			public RawMutation noRemark() {
				return new FullMutation();
			}

		}

		public class FullMutation extends RawMutation {
			public MutationBuilder<CompleteMutationWithoutBalanceBeforeAndAfterSpecification> addMutationCopyBalanceAfter() {

				finalizeMutation();
				return new MutationBuilder<CompleteMutationWithoutBalanceBeforeAndAfterSpecification>(mutation);

			}
		}

		public class RawMutation implements StepAfterStepSetDescription {
			public Mutation get() {
				finalizeMutation();
				return mutation;
			}

			public MutationBuilder<T> addMutation() {
				Mutation previousMutation = mutation;
				mutation = new Mutation();
				mutation.previousMutation = previousMutation;

				return MutationBuilder.this;
			}

			// Step to determine and set the ordernumber and previous mutatiom.
			protected void finalizeMutation() {
				mutation.ordernumber = 1;
				Mutation previousMutation = mutation.previousMutation;
				if (previousMutation != null && previousMutation.interestDate != null && previousMutation.interestDate.equals(mutation.interestDate)) {
					mutation.ordernumber += previousMutation.ordernumber;
				}
			}
		}
	}

}
