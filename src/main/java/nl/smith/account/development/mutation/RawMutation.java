package nl.smith.account.development.mutation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import nl.smith.account.enums.persisted.AccountNumber;

public class RawMutation {

	protected Integer id;

	@NotNull
	private AccountNumber accountNumber;

	@Min(value = 1, groups = RawMutationField.class)
	private int pageNumber;

	@NotNull
	private BigDecimal amount;

	@PastOrPresent
	@NotNull
	private LocalDate transactionDate;

	@NotNull
	@NotEmpty
	private String description;

	@NotNull
	@Min(1)
	private Integer ordernumber;

	// Not persisted. */
	private RawMutation previousMutation;

	// Used by MyBatis
	protected RawMutation() {
	}

	// Used by MyBatis
	private void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public AccountNumber getAccountNumber() {
		return accountNumber;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public BigDecimal getAmount() {
		return amount;
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

	public Optional<RawMutation> getPreviousMutation() {
		return Optional.ofNullable(previousMutation);
	}

	@Override
	public String toString() {
		// @formatter:off
			
			return "Mutation [" +
					"\naccountNumber                   = " + accountNumber +
					"\npageNumber                      = " + pageNumber +
					"\namount                          = " + amount +	 
					"\ntransactionDate                 = " + transactionDate + 
					"\ndescription                     = " + description +
					"\nordernumber                     = " + ordernumber +
					(getPreviousMutation().isPresent() ? (
					"\namount previous mutation = " + getPreviousMutation().get().getAmount()) : "") + 
					"]";
			// @formatter:on
	}
}
