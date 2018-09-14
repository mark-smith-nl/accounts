package nl.smith.account.persistence;

import java.util.List;

import nl.smith.account.domain.Mutation;

public interface MutationMapper {

	void persist(Mutation mutation);

	void deleteAll();

	List<Mutation> getMutations();
}
