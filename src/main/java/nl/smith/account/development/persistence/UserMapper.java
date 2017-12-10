package nl.smith.account.development.persistence;

import nl.smith.account.development.domain.User;

public interface UserMapper {
	User getUserByUsername(String username);
}
