package nl.smith.account;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import nl.smith.account.domain.PersistedEnum;
import nl.smith.account.enums.Currency;
import nl.smith.account.service.EnumService;

@SpringBootApplication
public abstract class Application {

	private final static Logger LOGGER = LoggerFactory.getLogger(Application.class);

	private final EnumService enumService;

	public Application(EnumService enumService) {
		super();
		this.enumService = enumService;
	}

	public static void main(String[] args) throws Exception {
		SpringApplication springApplication = new SpringApplication(Application.class);

		ConfigurableApplicationContext context = springApplication.run();

		EnumService enumService = context.getBean(EnumService.class);

		enumService.synchronizePersistedEnums();

		// Class<Currency> a = Currency.class;
		List<PersistedEnum> persistedEnums = enumService.getPersistedEnums(Currency.class);
		persistedEnums.forEach(System.out::println);
	}

}
