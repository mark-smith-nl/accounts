package nl.smith.account.service;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import nl.smith.account.domain.Mutation;
import nl.smith.account.persistence.MutationMapper;

@Validated
@Service
@Transactional
public class MutationService {

	private final static Logger LOGGER = LoggerFactory.getLogger(MutationService.class);

	private final MutationMapper accountMapper;

	public MutationService(MutationMapper accountMapper) {
		this.accountMapper = accountMapper;
	}

	public void removeTransactions() {
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
