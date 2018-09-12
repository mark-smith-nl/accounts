package nl.smith.account.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import nl.smith.account.domain.Mutation;
import nl.smith.account.enums.persisted.Currency;
import nl.smith.account.service.MutationService;
import nl.smith.account.web.AuthenticatedUserService;

@Controller
@RequestMapping("mutation")
public class MutationController extends AbstractController {

	private final MutationService mutationService;

	public MutationController(AuthenticatedUserService authenticatedUserService, MutationService mutationService) {
		super(authenticatedUserService);
		this.mutationService = mutationService;
	}

	@GetMapping("all")
	private String getAll(ModelMap modelMap) {
		System.out.println("===========>" + authenticatedUserService.getUserName());
		System.out.println("===========>" + authenticatedUserService.roles());
		modelMap.addAttribute("mutations", mutationService.getMutations());

		List<Mutation> mutations = new ArrayList<>();

		// @formatter:off
		mutations.add(
				Mutation.MutationBuilder.create("R449937763", Currency.EUR)
				.setBalanceBefore(BigDecimal.TEN)
				.setBalanceAfter(BigDecimal.TEN)
				.setAmount(BigDecimal.ZERO)
				.setInterestAndTransactionDate(LocalDate.now())
				.setDescription("Osama").get());
		mutations.add(Mutation.MutationBuilder.create("R449937763",Currency.EUR)
				.setBalanceBefore(BigDecimal.TEN)
				.setBalanceAfter(BigDecimal.TEN)
				.setAmount(BigDecimal.ZERO)
				.setInterestAndTransactionDate(LocalDate.now())
				.setDescription("Bokassa").get());
		mutations.add(Mutation.MutationBuilder.create("R449937763", Currency.EUR)
				.setBalanceBefore(BigDecimal.TEN)
				.setBalanceAfter(BigDecimal.TEN)
				.setAmount(BigDecimal.ZERO)
				.setInterestAndTransactionDate(LocalDate.now())
				.setDescription("Idi").get());
		// @formatter:on

		mutationService.persist(mutations);

		return "mutations.html";
	}
}
