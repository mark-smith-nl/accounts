package nl.smith.account.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import nl.smith.account.domain.Mutation;
import nl.smith.account.persistence.AccountMapper;

@Validated
@Service
@Transactional
public class MutationService {

	private final static Logger LOGGER = LoggerFactory.getLogger(MutationService.class);

	private final AccountMapper accountMapper;

	@Autowired
	public MutationService(AccountMapper accountMapper) {
		this.accountMapper = accountMapper;
	}

	public void removeTransactions() {
		accountMapper.deleteAll();
	}

	public void persist(@Valid List<Mutation> mutations) {
		for (Mutation mutation : mutations) {
			this.persist(mutation);
		}

		postProcess();
	}

	public void persist(@Valid Mutation mutation) {
		accountMapper.persist(mutation);

		// System.out.println(validator);
	}

	private void postProcess() {
		List<Date> transactionDatesToBePostProcessed = accountMapper.getTransactionDatesToBePostProcessed();

		transactionDatesToBePostProcessed.forEach(date -> {
			List<Integer> mutationIdsToBeprocessedForDate = accountMapper.getMutationIdsToBeprocessedForDate(date);
			LOGGER.info("Postprocess {} mutations for date {}.", mutationIdsToBeprocessedForDate.size(), date);

			int ordernumber = 0;
			for (Integer id : mutationIdsToBeprocessedForDate) {
				Map<String, Integer> parameterMap = new HashMap<>();
				parameterMap.put("id", id);
				parameterMap.put("ordernumber", ++ordernumber);
				accountMapper.setOrdernumberForMutationWithId(parameterMap);
			}
		});

	}

}
