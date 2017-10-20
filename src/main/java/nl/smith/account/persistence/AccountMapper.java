package nl.smith.account.persistence;

import java.util.Date;
import java.util.List;
import java.util.Map;

import nl.smith.account.domain.Mutation;

public interface AccountMapper {
	void persist(Mutation mutation);

	List<Date> getTransactionDatesToBePostProcessed();

	List<Integer> getMutationIdsToBeprocessedForDate(Date date);

	void setOrdernumberForMutationWithId(Map<String, Integer> parameterMap);

	void deleteAll();
}
