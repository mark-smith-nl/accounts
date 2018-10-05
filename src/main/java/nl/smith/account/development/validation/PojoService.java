package nl.smith.account.development.validation;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class PojoService {

	@Validated(FieldOne.class)
	public void hasFirstName(@Valid SimplePojo pojo) {
		System.out.println("Valid - has first name");
	}

	@Validated(FieldTwo.class)
	public void hasLastName(@Valid SimplePojo pojo) {
		System.out.println("Valid - has last name");
	}

	public SimplePojo getPojo() {
		return new SimplePojo();
	}

	@Validated({ FieldOne.class, FieldTwo.class })
	public void hasFirstAndLastName(@NotNull @Valid SimplePojo simplePojo) {
		System.out.println("Valid - has first and last name");

	}
}
