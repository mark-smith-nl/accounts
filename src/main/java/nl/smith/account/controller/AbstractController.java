package nl.smith.account.controller;

import nl.smith.account.service.AuthenticatedUserService;

public class AbstractController {

	protected final AuthenticatedUserService authenticatedUserService;

	protected AbstractController(AuthenticatedUserService authenticatedUserService) {
		this.authenticatedUserService = authenticatedUserService;
	}

}
