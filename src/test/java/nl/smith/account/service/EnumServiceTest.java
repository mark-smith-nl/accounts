package nl.smith.account.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import nl.smith.account.Application;
import nl.smith.account.enums.persisted.AbstractPersistedEnum;
import nl.smith.account.enums.persisted.Currency;
import nl.smith.account.enums.persisted.MutationType;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class EnumServiceTest {

	@Autowired
	private EnumService enumService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	public void getAnnotatedEnumClasses() {
		List<Class<AbstractPersistedEnum>> annotatedEnumClasses = EnumService.getPersistedEnumClasses();

		assertThat(annotatedEnumClasses.size(), is(2));

		assertTrue(annotatedEnumClasses.contains(Currency.class));
		assertTrue(annotatedEnumClasses.contains(MutationType.class));

		annotatedEnumClasses.forEach(annotatedEnumClass -> {
			List<AbstractPersistedEnum> enumConstants = List.of(annotatedEnumClass.getEnumConstants());
			String sql = "select * from " + annotatedEnumClass.getSimpleName();
			Map<String, Map<String, Object>> tupleMap = asEnumMap(jdbcTemplate.queryForList(sql));
			assertThat(tupleMap.size(), is(enumConstants.size()));
			enumConstants.forEach(abstractEnum -> {
				String enumName = abstractEnum.name();
				Map<String, Object> persistedEnum = tupleMap.get(enumName);
				assertNotNull(persistedEnum);
				assertThat(persistedEnum.get("description"), is(abstractEnum.getDescription()));
				assertThat(persistedEnum.get("isDefaultValue"), is(abstractEnum.isDefaultValue()));
				assertThat(persistedEnum.get("isActiveValue"), is(true));
			});
		});
	}

	private static Map<String, Map<String, Object>> asEnumMap(List<Map<String, Object>> queryForList) {
		Map<String, Map<String, Object>> enumMap = new HashMap<>();

		queryForList.forEach(tuple -> {
			String enumName = (String) tuple.get("name");
			enumMap.put(enumName, tuple);
		});
		return enumMap;
	}

}
