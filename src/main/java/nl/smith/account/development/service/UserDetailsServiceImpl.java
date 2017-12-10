package nl.smith.account.development.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import nl.smith.account.development.domain.User;
import nl.smith.account.development.domain.UserDetailImpl;
import nl.smith.account.development.persistence.UserMapper;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserMapper userMapper;

	@Override
	public UserDetails loadUserByUsername(String username) {
		User user = userMapper.getUserByUsername(username);

		if (user == null) {
			throw new UsernameNotFoundException(username);
		}

		return new UserDetailImpl(user);
	}

}
