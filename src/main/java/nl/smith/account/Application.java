package nl.smith.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import nl.smith.account.development.PojoOne;
import nl.smith.account.service.EnumService;
import nl.smith.account.service.MutationService;

@SpringBootApplication
public abstract class Application {

	public static void main(String[] args) throws Exception {
		SpringApplication springApplication = new SpringApplication(Application.class);

		ConfigurableApplicationContext context = springApplication.run();
		System.out.println(context.getBean(Application.class).getClass());
		System.out.println(context.getBean(EnumService.class).getClass());
		System.out.println(context.getBean(MutationService.class).getClass());

		EnumService enumService = context.getBean(EnumService.class);

		System.out.println(enumService.pojoOne());
		System.out.println(enumService.pojoOne());
		System.out.println(enumService.pojoOne());
		System.out.println(enumService.pojoOne());
	}

	/*
	 * @Bean
	 * 
	 * @Scope public PojoOne pojoOne() { return new PojoOne("Mark", 53); }
	 * 
	 * @Bean
	 * 
	 * @Scope public static PojoOne pojoOne() { return new PojoOne("Mark", 53); }
	 */

	@Bean
	@Scope("prototype")
	public PojoOne pojoOne() {
		return new PojoOne("Mark", 53);
	}

}
