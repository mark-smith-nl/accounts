package nl.smith.account.validation;

import java.math.BigDecimal;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nl.smith.account.annotation.ValidMutation;
import nl.smith.account.domain.Mutation;

public class MutationValidator implements ConstraintValidator<ValidMutation, Mutation> {

	private double allowableBalanceDifference;

	@Override
	public void initialize(ValidMutation constraintAnnotation) {
		allowableBalanceDifference = constraintAnnotation.allowableBalanceDifference() + 2;
	}

	@Override
	public boolean isValid(Mutation mutation, ConstraintValidatorContext context) {
		boolean valid = true;

		// TODO extra validation. Use context
		if (mutation != null) {
			BigDecimal balanceAfter = mutation.getBalanceAfter();
			BigDecimal balanceBefore = mutation.getBalanceBefore();
			BigDecimal amount = mutation.getAmount();

			int difference = balanceAfter.subtract(balanceBefore).subtract(amount).abs().intValue();

			if (difference >= allowableBalanceDifference) {
				valid = false;
			}

			if (mutation.getPreviousMutation().isPresent()) {
				balanceAfter = mutation.getPreviousMutation().get().getBalanceAfter();
				balanceBefore = mutation.getBalanceBefore();

				difference = balanceAfter.subtract(balanceBefore).abs().intValue();
				if (difference >= allowableBalanceDifference) {
					valid = false;
				}
			}
		}

		return valid;
	}

}
