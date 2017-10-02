package nl.smith.account.persistence;

import java.util.Date;
import java.util.List;

import nl.smith.account.domain.Mutation;

public interface AccountMapper {
    void persist(Mutation mutation);

    List<Date> getTransactionDatesForMutations();
}
