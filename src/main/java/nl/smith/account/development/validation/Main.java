package nl.smith.account.development.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Main {

	@Autowired
	private PojoService pojoService;

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(Main.class);

		ConfigurableApplicationContext context = springApplication.run();

		Main application = context.getBean(Main.class);

		PojoService pojoService = application.pojoService;

		SimplePojo simplePojo = new SimplePojo();
		try {
			pojoService.hasFirstName(simplePojo);
		} catch (ConstraintViolationException e) {
			System.err.println(e.getMessage());
			Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
		}

		simplePojo.setFirstName("Mark");
		pojoService.hasFirstName(simplePojo);

		try {
			pojoService.hasLastName(simplePojo);
		} catch (ConstraintViolationException e) {
			System.err.println(e.getMessage());
		}

		simplePojo.setLastName("Smith");
		pojoService.hasLastName(simplePojo);

		simplePojo = new SimplePojo();
		try {
			pojoService.hasFirstAndLastName(simplePojo);
		} catch (ConstraintViolationException e) {
			System.err.println(e.getMessage());
		}

		try {
			pojoService.hasFirstAndLastName(null);
			System.out.println("Yesssss");
		} catch (ConstraintViolationException e) {
			System.err.println(e.getMessage());
		}
		// SimplePojo pojo = pojoService.getPojo();
		// System.out.println(pojoService.getClass().getCanonicalName());
	}
}
