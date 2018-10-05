package nl.smith.account.development.test;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import nl.smith.account.development.test.Pojo.PojoBuilder.CompletePojo;

@SpringBootApplication()
public class Main {

	@Autowired
	private Validator validator;

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(Main.class);

		ConfigurableApplicationContext context = springApplication.run();

		Main main = context.getBean(Main.class);

		Validator validator = main.validator;

		String value = "-";
// @formatter:off
		Pojo pojo = Pojo.PojoBuilder.getBuilder(CompletePojo.class)
				.setFieldOne(value)
				.setFieldTwo(value)
				.setFieldThree(value)
				.setFieldFour(value)
				.setFieldFive123(value)
				.get();
		// @formatter:on	

		Set<ConstraintViolation<Pojo>> constraintValidations = validator.validate(pojo, Pojo.ValidateRawPojo.class);

		constraintValidations.forEach(System.out::println);

		constraintValidations = validator.validate(pojo, Pojo.ValidateCompletePojo.class);

		constraintValidations.forEach(System.out::println);
	}

}
