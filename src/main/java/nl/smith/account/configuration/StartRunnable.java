package nl.smith.account.configuration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import nl.smith.account.domain.Mutation;
import nl.smith.account.enums.Currency;
import nl.smith.account.service.MutationService;

@Component
public class StartRunnable implements CommandLineRunner {

	private final MutationService mutationService;

	public StartRunnable(MutationService mutationService) {
		this.mutationService = mutationService;
	}

	@Override
	public void run(String... args) throws Exception {
		List<Mutation> mutations = new ArrayList<>();

		// @formatter:off
		mutations.add(
				Mutation.MutationBuilder.create()
				.setAccountNumber("449937763")
				.setCurrency(Currency.EUR)
				.setTransactionDate(new Date())
				.setBalanceBefore(BigDecimal.TEN)
				.setBalanceAfter(BigDecimal.TEN)
				.setInterestDate(new Date())
				.setAmount(BigDecimal.ZERO)
				.setDescription("Osama").get());
		mutations.add(Mutation.MutationBuilder.create()
				.setAccountNumber("449937763")
				.setCurrency(Currency.EUR)
				.setTransactionDate(new Date())
				.setBalanceBefore(BigDecimal.TEN)
				.setBalanceAfter(BigDecimal.TEN)
				.setInterestDate(new Date())
				.setAmount(BigDecimal.ZERO)
				.setDescription("Bokassa").get());
		mutations.add(Mutation.MutationBuilder.create()
				.setAccountNumber("449937763")
				.setCurrency(Currency.EUR)
				.setTransactionDate(new Date())
				.setBalanceBefore(BigDecimal.TEN)
				.setBalanceAfter(BigDecimal.TEN)
				.setInterestDate(new Date())
				.setAmount(BigDecimal.ZERO)
				.setDescription("Idi").get());
		// @formatter:on

		// mutationService.persist(mutations);
	}

}
