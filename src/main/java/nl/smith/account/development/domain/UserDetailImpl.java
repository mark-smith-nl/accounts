package nl.smith.account.development.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailImpl implements UserDetails {

	// private static final Pattern PASSWORD_REGEX =
	// Pattern.compile("(\\$(\\d)a?\\$(\\d\\d))\\$(\\S+)");

	private final String username;

	private final String password;

	private final boolean accountNonExpired;

	private final boolean accountNonLocked;

	private final boolean credentialsNonExpired;

	private final boolean enabled;

	private final List<? extends GrantedAuthority> authorities;

	private final String email;

	public UserDetailImpl(User user) {
		username = user.getUsername();

		/*
		 * String password = user.getPassword(); if (password == null ||
		 * !password.matches(PASSWORD_REGEX.pattern())) { throw new
		 * IllegalStateException("Illegal password"); }
		 */
		this.password = user.getPassword();

		accountNonExpired = user.isAccountNonExpired();
		accountNonLocked = user.isAccountNonLocked();
		credentialsNonExpired = user.isAccountNonExpired();
		enabled = user.isEnabled();

		List<GrantedAuthority> authorities = new ArrayList<>();
		user.getRoles().forEach(role -> {
			authorities.add(new GrantedAuthority() {

				@Override
				public String getAuthority() {
					return role;
				}
			});

		});

		this.authorities = Collections.unmodifiableList(authorities);

		email = user.getEmail();
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		// Matcher matcher = PASSWORD_REGEX.matcher(password);
		// matcher.matches();
		// return matcher.group(4);
		return password;
	}

	public String getEncryptionTypeAndSalt() {
		/*
		 * Matcher matcher = PASSWORD_REGEX.matcher(password);
		 * matcher.matches(); return matcher.group(1);
		 */
		return null;
	}

	public int getEncryptionType() {
		/*
		 * Matcher matcher = PASSWORD_REGEX.matcher(password);
		 * matcher.matches(); return Integer.valueOf(matcher.group(2));
		 */
		return 0;
	}

	public String getSalt() {
		/*
		 * Matcher matcher = PASSWORD_REGEX.matcher(password);
		 * matcher.matches(); return null; // return matcher.group(3);
		 */
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public List<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public String getEmail() {
		return email;
	}

}
