package nl.smith.account.development.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailImpl implements UserDetails {

	private static final Pattern PASSWORD_REGEX = Pattern.compile("(\\$(\\d)a?\\$(\\d\\d))\\$(\\S+)");

	private final String username;

	private final String password;

	private final boolean accountNonExpired;

	private final boolean accountNonLocked;

	private final boolean credentialsNonExpired;

	private final boolean enabled;

	private final Collection<? extends GrantedAuthority> authorities;

	private final String email;

	public UserDetailImpl(User user) {
		username = user.getUsername();

		String password = user.getPassword();
		if (password == null || !password.matches(PASSWORD_REGEX.pattern())) {
			throw new IllegalStateException("Illegal password");
		}
		this.password = user.getPassword();

		accountNonExpired = user.isAccountNonExpired();
		accountNonLocked = user.isAccountNonLocked();
		credentialsNonExpired = user.isAccountNonExpired();
		enabled = user.isEnabled();

		Set<GrantedAuthority> authorities = new HashSet<>();
		user.getRoles().forEach(role -> {
			authorities.add(new GrantedAuthority() {

				@Override
				public String getAuthority() {
					return role;
				}
			});

		});

		this.authorities = Collections.unmodifiableSet(authorities);

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
		Matcher matcher = PASSWORD_REGEX.matcher(password);
		matcher.matches();
		return matcher.group(1);
	}

	public int getEncryptionType() {
		Matcher matcher = PASSWORD_REGEX.matcher(password);
		matcher.matches();
		return Integer.valueOf(matcher.group(2));
	}

	public String getSalt() {
		Matcher matcher = PASSWORD_REGEX.matcher(password);
		matcher.matches();
		return null;
		// return matcher.group(3);
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
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public String getEmail() {
		return email;
	}

}
