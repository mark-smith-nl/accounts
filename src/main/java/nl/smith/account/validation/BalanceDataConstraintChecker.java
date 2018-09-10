package nl.smith.account.validation;

import java.math.BigDecimal;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nl.smith.account.annotation.ValidBalanceData;
import nl.smith.account.domain.Mutation;

public class BalanceDataConstraintChecker implements ConstraintValidator<ValidBalanceData, Mutation> {

	private double allowableBalanceDifference;

	private BigDecimal balanceBefore;
	private BigDecimal balanceAfter;
	private BigDecimal amount;

	@Override
	public void initialize(ValidBalanceData constraintAnnotation) {
		allowableBalanceDifference = constraintAnnotation.allowableBalanceDifference() + 2;

	}

	@Override
	public boolean isValid(Mutation mutation, ConstraintValidatorContext context) {
		boolean valid = true;

		if (mutation != null) {
			balanceAfter = mutation.getBalanceAfter();
			balanceBefore = mutation.getBalanceBefore();
			amount = mutation.getAmount();

			if (balanceAfter.min(balanceBefore).min(amount).abs().intValue() >= allowableBalanceDifference) {
				valid = false;
			}

		}
		return valid;
	}

}
