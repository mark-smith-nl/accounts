package nl.smith.account.controller;

import nl.smith.account.web.AuthenticatedUserService;

public class AbstractController {

	protected final AuthenticatedUserService authenticatedUserService;

	protected AbstractController(AuthenticatedUserService authenticatedUserService) {
		this.authenticatedUserService = authenticatedUserService;
	}

}
