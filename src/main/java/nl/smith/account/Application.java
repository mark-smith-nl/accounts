package nl.smith.account;

import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import nl.smith.account.domain.Mutation;
import nl.smith.account.enums.persisted.AccountNumber;
import nl.smith.account.enums.persisted.Currency;
import nl.smith.account.service.MutationService;

@SpringBootApplication
public abstract class Application {

	public static void main(String[] args) throws Exception {
		SpringApplication springApplication = new SpringApplication(Application.class);

		ConfigurableApplicationContext context = springApplication.run();

		// @formatter:off
 
		Mutation mutation = Mutation.MutationBuilder
				.create()
				.setAccountNumber(AccountNumber.R449937763)
				.setCurrency(Currency.EUR)
				.setTransactionDate(new Date())
				.setBalanceBefore(1250)
				.setBalanceAfter(1300)
				.setInterestDate(new Date())
				.setAmount(150)
				.setDescription("50 erbij ==> 1300")
				.setRemark("Met een opmerking...")
				.get();

		// @formatter:on

		MutationService mutationService = context.getBean(MutationService.class);
		mutationService.persist(mutation);
		mutationService.getMutations().forEach(System.out::println);
	}

}
