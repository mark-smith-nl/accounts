package nl.smith.account.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nl.smith.account.annotation.ValidBalanceData;
import nl.smith.account.domain.Mutation;

public class BalanceDataConstraintChecker implements ConstraintValidator<ValidBalanceData, Mutation> {

	private double allowableBalanceDifference;

	@Override
	public void initialize(ValidBalanceData constraintAnnotation) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isValid(Mutation mutation, ConstraintValidatorContext context) {
		boolean valid = true;

		if (mutation != null) {
			if (mutation.getAmount().abs().intValue() < 3) {
				valid = false;
			}
		}
		return valid;
	}

}
