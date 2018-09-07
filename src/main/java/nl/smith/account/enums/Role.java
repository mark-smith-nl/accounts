package nl.smith.account.enums;

import org.springframework.security.core.GrantedAuthority;

/** All web roles. */
public enum Role implements AbstractEnum {
	ADMIN("Administrator: Can modify all settings and all content."),
	EDITOR("Editor: Can modify all content."),
	AUTHOR("Author: Can modify owned content."),
	READER("Reader: Can not modify any content.");

	private final String description;

	private Role(String description) {
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public static Role getRole(GrantedAuthority grantedAuthority) {
		String role = grantedAuthority.getAuthority().replaceAll("ROLE_", "");

		return Role.valueOf(role);
	}

	@Override
	public String toString() {
		return String.format("%s.%s[%s]", getClass().getSimpleName(), name(), getDescription());
	}
}
