package nl.smith.account.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import nl.smith.account.enums.Role;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http
			.authorizeRequests()
				.antMatchers("/", "/public-*", "/webjars/**").permitAll()
				.anyRequest().authenticated()
			.and()
				.formLogin()
					.loginPage("/public-customizedloginPage")
					.permitAll()
			.and()
				.logout().permitAll();
		// @formatter:on

	}

	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		// @formatter:off
		UserDetails admin =
	             User.withDefaultPasswordEncoder()
	                .username("Administrator")
	                .password("password")
	                .roles(Role.ADMIN.name())
	                .build();
		
		UserDetails editor =
	             User.withDefaultPasswordEncoder()
	                .username("Editor")
	                .password("password")
	                .roles(Role.EDITOR.name())
	                .build();

		UserDetails author =
	             User.withDefaultPasswordEncoder()
	                .username("Author")
	                .password("password")
	                .roles(Role.AUTHOR.name())
	                .build();

		UserDetails reader =
	             User.withDefaultPasswordEncoder()
	                .username("Reader")
	                .password("password")
	                .roles(Role.READER.name())
	                .build();
	    // @formatter:on

		return new InMemoryUserDetailsManager(admin, editor, author, reader);
	}

}
