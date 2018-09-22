package nl.smith.account.persistence;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import nl.smith.account.domain.Mutation;
import nl.smith.account.domain.RawMutation;

public interface MutationMapper {

	void persist(Mutation mutation);

	void deleteAll();

	List<Mutation> getMutations();

	void persistRawMutation(@NotNull @Valid RawMutation rawMutation);
}
