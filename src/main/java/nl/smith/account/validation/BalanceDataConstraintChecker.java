package nl.smith.account.validation;

import java.math.BigDecimal;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.smith.account.annotation.ValidBalanceData;
import nl.smith.account.domain.Mutation;

public class BalanceDataConstraintChecker implements ConstraintValidator<ValidBalanceData, Mutation> {

	private final static Logger LOGGER = LoggerFactory.getLogger(BalanceDataConstraintChecker.class);

	private double allowableBalanceDifference;

	@Override
	public void initialize(ValidBalanceData constraintAnnotation) {
		allowableBalanceDifference = constraintAnnotation.allowableBalanceDifference() + 2;
	}

	@Override
	public boolean isValid(Mutation mutation, ConstraintValidatorContext context) {
		boolean valid = true;

		if (mutation != null) {
			BigDecimal balanceAfter = mutation.getBalanceAfter();
			BigDecimal balanceBefore = mutation.getBalanceBefore();
			BigDecimal amount = mutation.getAmount();

			int difference = balanceAfter.subtract(balanceBefore).subtract(amount).abs().intValue();

			if (difference >= allowableBalanceDifference) {
				LOGGER.debug("Invalid mutation.\nBalance after: {}\nBalance after: {}\nAmount: {}\nDifference: {}", balanceAfter, balanceBefore, amount, difference);
				valid = false;
			}

			if (mutation.getPreviousMutation().isPresent()) {
				balanceAfter = mutation.getPreviousMutation().get().getBalanceAfter();
				balanceBefore = mutation.getBalanceBefore();

				difference = balanceAfter.subtract(balanceBefore).abs().intValue();
				if (difference >= allowableBalanceDifference) {
					LOGGER.debug("Invalid mutation.\nBalance after: {}\nBalance after: {}\nAmount: {}\nDifference: {}", balanceAfter, balanceBefore, amount, difference);
					valid = false;
				}
			}

		}

		return valid;
	}

}
