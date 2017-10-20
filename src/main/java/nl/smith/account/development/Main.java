package nl.smith.account.development;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import nl.smith.account.domain.Mutation;

public class Main {
	public static void main(String[] args) {
		// Validator validator = new LocalValidatorFactoryBean();

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		Mutation mutation = new Mutation();

		Set<ConstraintViolation<Mutation>> violations = validator.validate(mutation);

		System.out.println(violations.size());

		violations.forEach(violation -> {
			System.out.println(violation.getInvalidValue());
			System.out.println(violation.getMessage());
			System.out.println(violation.getPropertyPath());
		});
	}
}
