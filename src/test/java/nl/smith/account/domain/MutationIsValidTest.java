package nl.smith.account.domain;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import nl.smith.account.AbstractTest;
import nl.smith.account.enums.persisted.AccountNumber;
import nl.smith.account.enums.persisted.Currency;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { MutationIsValidTest.class, MutationMatcher.class })
@EnableAutoConfiguration
public class MutationIsValidTest extends AbstractTest {

	@Autowired
	private MutationMatcher isValid;

	@Test
	public void mutation_empty() {
		assertThat(null, not(isValid));
	}

	@Test
	public void mutation_wrongBalanceAfter() {
		// @formatter:off
		Mutation mutation = Mutation.MutationBuilder
			.create(AccountNumber.R449937763, Currency.EUR)
			.setBalanceBefore(100)
			.setBalanceAfter(200)
			.setAmount(2)
			.setInterestAndTransactionDate(LocalDate.now())
			.setDescription("Wrong balance after")
			.get();
		
		// @formatter:on

		assertThat(mutation, not(isValid));
	}

	@Test
	public void mutation_wrongBalanceAfter2() {
		// @formatter:off
		Mutation mutation = Mutation.MutationBuilder
			.create(AccountNumber.R449937763, Currency.EUR)
			.setBalanceBefore(100)
			.setBalanceAfter(200)
			.setAmount(2)
			.setInterestAndTransactionDate(LocalDate.now())
			.setDescription("Wrong balance after")
			.get();
		
		Mutation.MutationBuilder
		.create(AccountNumber.R449937763, Currency.EUR)
		.setBalanceBefore(100)
		.setBalanceAfter(200)
		.setAmount(100)
		.setInterestAndTransactionDate(LocalDate.now())
		.setDescription("Wrong balance after")
		.add(mutation)
		.getAll();
		// @formatter:on

		assertThat(mutation, not(isValid));
	}

	@Test
	public void mutation_correctBalanceAfter() {
		// @formatter:off
		Mutation mutation = Mutation.MutationBuilder
			.create(AccountNumber.R449937763, Currency.EUR)
			.setBalanceBefore(100)
			.setBalanceAfter(200)
			.setAmount(100)
			.setInterestAndTransactionDate(LocalDate.now())
			.setDescription("Correct balance after")
			.get();
		
		// @formatter:on

		assertThat(mutation, isValid);
	}
}
