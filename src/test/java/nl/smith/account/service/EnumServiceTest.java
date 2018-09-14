package nl.smith.account.service;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import nl.smith.account.AbstractTest;
import nl.smith.account.domain.PersistedEnum;
import nl.smith.account.enums.persisted.AbstractPersistedEnum;
import nl.smith.account.enums.persisted.AccountNumber;
import nl.smith.account.enums.persisted.Currency;
import nl.smith.account.enums.persisted.MutationType;
import nl.smith.account.persistence.PersistedEnumMapper;

public class EnumServiceTest extends AbstractTest {

	@Autowired
	private PersistedEnumMapper persistedEnumMapper;

	@Test
	public void synchronizePersistedEnum() {
		List<Class<AbstractPersistedEnum>> annotatedEnumClasses = EnumService.getPersistedEnumClasses();

		assertThat(annotatedEnumClasses.size(), is(3));

		assertTrue(annotatedEnumClasses.contains(Currency.class));
		assertTrue(annotatedEnumClasses.contains(MutationType.class));
		assertTrue(annotatedEnumClasses.contains(AccountNumber.class));

		annotatedEnumClasses.forEach(annotatedEnumClass -> {
			List<AbstractPersistedEnum> enumConstants = List.of(annotatedEnumClass.getEnumConstants());
			String tableName = EnumService.getTableName(annotatedEnumClass);

			Map<String, PersistedEnum> persistedEnums = persistedEnumMapper.getPersistedEnums(tableName).stream()
					.collect(Collectors.toMap(persistedEnum -> persistedEnum.name(), persistedEnum -> persistedEnum));

			assertThat(persistedEnums.size(), greaterThanOrEqualTo(enumConstants.size()));

			enumConstants.forEach(abstractPersistedEnum -> {
				String enumName = abstractPersistedEnum.name();
				PersistedEnum persistedEnum = persistedEnums.get(enumName);
				assertNotNull(persistedEnum);
				assertThat(abstractPersistedEnum.getDescription(), is(persistedEnum.getDescription()));
				assertThat(abstractPersistedEnum.isDefaultValue(), is(persistedEnum.isDefaultValue()));
				assertThat(true, is(persistedEnum.isActiveValue()));

			});

		});
	}

}
