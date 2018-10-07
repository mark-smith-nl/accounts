package nl.smith.account.development.test;

<<<<<<< HEAD
import java.time.LocalDate;
=======
>>>>>>> 8cb01b139253789c47be042f84379a9e3c56654b
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

<<<<<<< HEAD
import nl.smith.account.development.test.Mutation.MutationBuilder;
import nl.smith.account.development.test.Mutation.MutationBuilder.FullMutation;
=======
import nl.smith.account.development.test.Pojo.PojoBuilder.CompletePojo;
>>>>>>> 8cb01b139253789c47be042f84379a9e3c56654b

@SpringBootApplication()
public class Main {

	@Autowired
	private Validator validator;

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(Main.class);

		ConfigurableApplicationContext context = springApplication.run();

		Main main = context.getBean(Main.class);

		Validator validator = main.validator;
<<<<<<< HEAD
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
=======

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
>>>>>>> 8cb01b139253789c47be042f84379a9e3c56654b
	}

}
