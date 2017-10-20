package nl.smith.account.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

import nl.smith.account.validation.BalanceDataConstraintChecker;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BalanceDataConstraintChecker.class)
public @interface ValidBalanceData {
	String message();

	double allowableBalanceDifference();
}
