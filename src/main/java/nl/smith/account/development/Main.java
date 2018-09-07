package nl.smith.account.development;

import java.util.Date;

import nl.smith.account.domain.Mutation;
import nl.smith.account.enums.persisted.AccountNumber;
import nl.smith.account.enums.persisted.Currency;

public abstract class Main {

	public static void main(String[] args) {
		Mutation mutation = Mutation.MutationBuilder.create().setAccountNumber(AccountNumber.R449937763).setCurrency(Currency.EUR).setTransactionDate(new Date())
				.setBalanceBefore(150).setBalanceAfter(200).setInterestDate(new Date()).setAmount(50).setDescription("50 erbij").get();

		Mutation.MutationBuilder.create().setAccountNumber(AccountNumber.R449937763).setCurrency(Currency.EUR).setTransactionDate(new Date()).setBalanceBefore(100)
				.setBalanceAfter(120).setInterestDate(new Date()).setAmount(30).setDescription("20 erbij").and().setAccountNumber(AccountNumber.R449937763)
				.setCurrency(Currency.EUR).setTransactionDate(new Date()).setBalanceBefore(120).setBalanceAfter(150).setInterestDate(new Date()).setAmount(50)
				.setDescription("30 erbij").and().add(mutation).getAll().forEach(System.out::println);
	}

}
