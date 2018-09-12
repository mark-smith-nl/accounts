package nl.smith.account.domain;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MutationMatcher extends BaseMatcher<Mutation> {

	private final static Logger LOGGER = LoggerFactory.getLogger(MutationMatcher.class);

	private final Validator validator;

	public MutationMatcher(Validator validator) {
		this.validator = validator;
	}

	@Override
	public boolean matches(Object item) {
		if (item instanceof Mutation) {
			Set<ConstraintViolation<Object>> violations = validator.validate(item);
			violations.forEach(violation -> LOGGER.warn("{}", violation));
			return validator.validate(item).size() == 0;
		}

		return false;
	}

	@Override
	public void describeTo(Description description) {
	}

}
