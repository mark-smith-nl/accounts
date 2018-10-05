package nl.smith.account.development.test;

import java.time.LocalDate;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import nl.smith.account.development.test.Mutation.MutationBuilder;
import nl.smith.account.development.test.Mutation.MutationBuilder.FullMutation;

@SpringBootApplication()
public class Main {

	@Autowired
	private Validator validator;

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(Main.class);

		ConfigurableApplicationContext context = springApplication.run();

		Main main = context.getBean(Main.class);

		Validator validator = main.validator;
		Mutation pojo = null;

		String value = "-";
// @formatter:off
		 FullMutation setBalanceAfter = Mutation.getBuilderFor(Mutation.MutationBuilder.CompleteMutation.class)
				.setAmount(5)
				.setTransactionDate(LocalDate.now())
				.setDescription("")
				.setBalanceBefore("")
				.setBalanceAfter("");
			// Should be parameterized	.
		 MutationBuilder addMutationCopyBalanceAfter = setBalanceAfter.addMutationCopyBalanceAfter();
				
		// @formatter:on	

		Set<ConstraintViolation<Mutation>> constraintValidations = validator.validate(pojo, Mutation.RawPojoFields.class);

		constraintValidations.forEach(System.out::println);

		constraintValidations = validator.validate(pojo, Mutation.CompletePojoFields.class);

		constraintValidations.forEach(System.out::println);

		constraintValidations.forEach(constraintValidation -> {
			System.out.println("===>" + constraintValidation.getPropertyPath());
		});

		constraintValidations.forEach(constraintValidation -> {
			System.out.println("====>" + constraintValidation.getMessageTemplate());
		});

		constraintValidations.forEach(constraintValidation -> {
			System.out.println("=====>" + constraintValidation.getMessage());
		});
	}

}
