package nl.smith.account.service;

import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import nl.smith.account.enums.Role;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AuthenticatedUserService {

	private final Authentication authentication;

	public AuthenticatedUserService(HttpServletRequest httpServletRequest) {
		authentication = (Authentication) httpServletRequest.getUserPrincipal();
	}

	public Set<String> roleNames() {
		return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
	}

	public Set<Role> roles() {
		return authentication.getAuthorities().stream().map(Role::getRole).collect(Collectors.toSet());
	}

	public String getUserName() {
		return authentication.getName();
	}

	public boolean hasRole(Role role) {
		return roles().contains(role);
	}

	public boolean hasAllRoles(Role... roles) {
		return roles().containsAll(Set.of(roles));
	}

}
