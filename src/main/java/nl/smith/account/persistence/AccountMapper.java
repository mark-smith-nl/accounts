package nl.smith.account.persistence;

import nl.smith.account.domain.Mutation;

public interface AccountMapper {
    void persist(Mutation mutation);
}
