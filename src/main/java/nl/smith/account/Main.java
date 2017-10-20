package nl.smith.account;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import nl.smith.account.service.ImportService;
import nl.smith.account.service.MutationService;

@SpringBootApplication
public abstract class Main implements CommandLineRunner {

	private final static Logger LOGGER = LoggerFactory.getLogger(Main.class);

	@Autowired
	private ImportService importService;
	@Autowired
	private MutationService mutationService;

	@Autowired
	private Validator validator;

	@Bean
	public Validator localValidatorFactoryBean() {
		return new LocalValidatorFactoryBean();
	}

	public static void main(String[] args) throws Exception {
		SpringApplication springApplication = new SpringApplication(Main.class);
		springApplication.setBannerMode(Banner.Mode.OFF);
		springApplication.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			importService.importFromFile("/home/mark/Downloads/TXT170929134202.TAB");
		} catch (ConstraintViolationException e) {
			e.getConstraintViolations().forEach(violation -> {
				System.out.println(violation.getMessage());
				System.out.println((violation.getInvalidValue()));
			});
		}
	}

}
