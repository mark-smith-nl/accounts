package nl.smith.account.service;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import nl.smith.account.domain.Mutation;
import nl.smith.account.persistence.MutationMapper;

@Validated
@Service
@Transactional
public class MutationService {

	private final MutationMapper accountMapper;

	public MutationService(MutationMapper accountMapper) {
		this.accountMapper = accountMapper;
	}

	public void removeMutations() {
		accountMapper.deleteAll();
	}

	public void persist(@NotEmpty @Valid List<Mutation> mutations) {
		mutations.forEach(this::persist);
	}

	public void persist(@NotNull @Valid Mutation mutation) {
		accountMapper.persist(mutation);
	}

	public List<Mutation> getMutations() {
		return accountMapper.getMutations();
	}

}
