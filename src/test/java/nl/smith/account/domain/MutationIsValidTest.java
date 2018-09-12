package nl.smith.account.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Stack;

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
	public void mutation_noAccountNumber() {
		// @formatter:off
		Mutation mutation = Mutation.MutationBuilder
			.create("-", Currency.EUR)
			.setBalanceBefore(100)
			.setBalanceAfter(200)
			.setAmount(100)
			.setInterestAndTransactionDate(LocalDate.now())
			.setDescription("Wrong balance after")
			.getMutation();
		// @formatter:on

		assertThat(mutation, not(isValid));
	}

	@Test
	public void mutation_noCurrency() {
		// @formatter:off
		Mutation mutation = Mutation.MutationBuilder
			.create(AccountNumber.R449937763, "-")
			.setBalanceBefore(100)
			.setBalanceAfter(200)
			.setAmount(100)
			.setInterestAndTransactionDate(LocalDate.now())
			.setDescription("Wrong balance after")
			.getMutation();
		// @formatter:on

		assertThat(mutation, not(isValid));
	}

	@Test
	public void mutation_noBalanceBefore() {
		// @formatter:off
		Mutation mutation = Mutation.MutationBuilder
			.create(AccountNumber.R449937763, Currency.EUR)
			.setBalanceBefore((BigDecimal) null)
			.setBalanceAfter(200)
			.setAmount(100)
			.setInterestAndTransactionDate(LocalDate.now())
			.setDescription("Wrong balance after")
			.getMutation();
		// @formatter:on

		assertThat(mutation, not(isValid));
	}

	@Test
	public void mutation_noBalanceAfter() {
		// @formatter:off
		Mutation mutation = Mutation.MutationBuilder
			.create(AccountNumber.R449937763, Currency.EUR)
			.setBalanceBefore(100)
			.setBalanceAfter((BigDecimal) null)
			.setAmount(100)
			.setInterestAndTransactionDate(LocalDate.now())
			.setDescription("Wrong balance after")
			.getMutation();
		// @formatter:on

		assertThat(mutation, not(isValid));
	}

	@Test
	public void mutation_noAmount() {
		// @formatter:off
		Mutation mutation = Mutation.MutationBuilder
			.create(AccountNumber.R449937763, Currency.EUR)
			.setBalanceBefore(100)
			.setBalanceAfter(200)
			.setAmount((BigDecimal) null)
			.setInterestAndTransactionDate(LocalDate.now())
			.setDescription("Wrong balance after")
			.getMutation();	
		// @formatter:on

		assertThat(mutation, not(isValid));
	}

	@Test
	public void mutation_noInterestAndTransactionDate() {
		// @formatter:off
		Mutation mutation = Mutation.MutationBuilder
			.create(AccountNumber.R449937763, Currency.EUR)
			.setBalanceBefore(100)
			.setBalanceAfter(200)
			.setAmount((BigDecimal) null)
			.setInterestAndTransactionDate((LocalDate) null)
			.setDescription("Wrong balance after")
			.getMutation();		
		// @formatter:on

		assertThat(mutation, not(isValid));
	}

	@Test
	public void mutation_futurenterestAndTransactionDate() {
		// @formatter:off
		Mutation mutation = Mutation.MutationBuilder
			.create(AccountNumber.R449937763, Currency.EUR)
			.setBalanceBefore(100)
			.setBalanceAfter(200)
			.setAmount(100)
			.setInterestAndTransactionDate(LocalDate.now().plusDays(1))
			.setDescription("Wrong balance after")
			.getMutation();		
		// @formatter:on

		assertThat(mutation, not(isValid));
	}

	@Test
	public void mutation_noDesription() {
		// @formatter:off
		Mutation mutation = Mutation.MutationBuilder
			.create(AccountNumber.R449937763, Currency.EUR)
			.setBalanceBefore(100)
			.setBalanceAfter(200)
			.setAmount((BigDecimal) null)
			.setInterestAndTransactionDate(LocalDate.now())
			.setDescription(null)
			.getMutation();
		// @formatter:on

		assertThat(mutation, not(isValid));
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
			.getMutation();
		// @formatter:on

		assertThat(mutation, not(isValid));
	}

	@Test
	public void mutation_wrongBalanceAfterStackOfMutations() {
		// @formatter:off
		Mutation mutation = Mutation.MutationBuilder
			.create(AccountNumber.R449937763, Currency.EUR)
			.setBalanceBefore(350)
			.setBalanceAfter(400)
			.setAmount(50)
			.setInterestAndTransactionDate(LocalDate.now())
			.setDescription("Second transaction")
			.getMutation();
		
		Stack<Mutation> mutations = Mutation.MutationBuilder
		.create(AccountNumber.R449937763, Currency.EUR)
		.setBalanceBefore(100)
		.setBalanceAfter(200)
		.setAmount(100)
		.setInterestAndTransactionDate(LocalDate.now())
		.setDescription("First transaction")
		.add(mutation)
		.getMutations();
		// @formatter:on

		assertThat(mutations.size(), is(2));

		Mutation lastMutation = mutations.pop();

		assertEquals(mutation, lastMutation);

		assertThat(mutation, not(isValid));

		Mutation firstMutation = mutations.pop();

		assertThat(firstMutation, isValid);
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
			.getMutation();
		// @formatter:on

		assertThat(mutation, isValid);
	}

	@Test
	public void mutation_correctBalanceAfterStackOfMutations() {
		// @formatter:off
		Mutation mutation = Mutation.MutationBuilder
			.create(AccountNumber.R449937763, Currency.EUR)
			.setBalanceBefore(200)
			.setBalanceAfter(400)
			.setAmount(200)
			.setInterestAndTransactionDate(LocalDate.now())
			.setDescription("Second transaction")
			.getMutation();
		
		Stack<Mutation> mutations = Mutation.MutationBuilder
		.create(AccountNumber.R449937763, Currency.EUR)
		.setBalanceBefore(100)
		.setBalanceAfter(200)
		.setAmount(100)
		.setInterestAndTransactionDate(LocalDate.now())
		.setDescription("First transaction")
		.add(mutation)
		.getMutations();
		// @formatter:on

		assertThat(mutations.size(), is(2));

		Mutation lastMutation = mutations.pop();

		assertEquals(mutation, lastMutation);

		assertThat(mutation, isValid);

		Mutation firstMutation = mutations.pop();

		assertThat(firstMutation, isValid);
	}
}
