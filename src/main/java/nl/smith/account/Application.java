package nl.smith.account;

import java.time.LocalDate;

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

		MutationService mutationService = context.getBean(MutationService.class);

		// s mutationService.removeTransactions();

		// @formatter:off
		mutationService.persist(Mutation.MutationBuilder
				.create(AccountNumber.R449937763, Currency.EUR)
				.setBalanceBefore(1250)
				.setBalanceAfter(1300)
				.setAmount(50)
				.setInterestAndTransactionDate(LocalDate.now())
				.setDescription("50 erbij 1250 ==> 1300")
				.setRemark("Met een opmerking...")
				.add()
				.setBalanceAfter(1500)
				.setAmount(200)
				.setInterestAndTransactionDate(LocalDate.now())
				.setDescription("200 erbij 1300 ==> 1500")
				.setRemark("Met een opmerking...")
				.add()
				.setBalanceAfter(1500)
				.setAmount(200)
				.setInterestAndTransactionDate(LocalDate.now())
				.setDescription("200 erbij 1300 ==> 1500")
				.setRemark("Met een opmerking...")
				.getMutations());
		// @formatter:on

		mutationService.getMutations().forEach(System.out::println);
	}

}
