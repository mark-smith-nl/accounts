package nl.smith.account.development.mutation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import nl.smith.account.annotation.ValidMutation;
import nl.smith.account.enums.persisted.Currency;
import nl.smith.account.validation.FieldChecks;

@GroupSequence({ FieldChecks.class, Mutation.class })
@ValidMutation(allowableBalanceDifference = 0.01)
@NotNull
public class Mutation extends RawMutation {

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

	private String getRemark() {
		return remark;
	}

	public Optional<String> getRemarkOption() {
		return Optional.ofNullable(remark);
	}

}
