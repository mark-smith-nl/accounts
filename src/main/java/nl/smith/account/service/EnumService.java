package nl.smith.account.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import nl.smith.account.annotation.PersistedEnum;
import nl.smith.account.enums.AbstractEnum;

@Service
public class EnumService {

	private final static Logger LOGGER = LoggerFactory.getLogger(EnumService.class);

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public EnumService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@PostConstruct
	private void persistEnums() throws IOException {
		List<Class<Enum<?>>> annotatedClasses = findAnnotatedClasses();

		InputStream inputStream = this.getClass().getResourceAsStream("createEnumTable.sql");
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		String createEnumTableSql = FileCopyUtils.copyToString(inputStreamReader);

		inputStream = this.getClass().getResourceAsStream("deactivateAllEnums.sql");
		inputStreamReader = new InputStreamReader(inputStream);
		String deactivateAllEnumsSql = FileCopyUtils.copyToString(inputStreamReader);

		inputStream = this.getClass().getResourceAsStream("getEnum.sql");
		inputStreamReader = new InputStreamReader(inputStream);
		String getEnumSql = FileCopyUtils.copyToString(inputStreamReader);

		inputStream = this.getClass().getResourceAsStream("persistEnum.sql");
		inputStreamReader = new InputStreamReader(inputStream);
		String persistEnumSql = FileCopyUtils.copyToString(inputStreamReader);

		inputStream = this.getClass().getResourceAsStream("updateEnum.sql");
		inputStreamReader = new InputStreamReader(inputStream);
		String updateEnumSql = FileCopyUtils.copyToString(inputStreamReader);

		// getEnumByValue.sql
		annotatedClasses.forEach(annotatedClass -> {

			updateEnums(annotatedClass, createEnumTableSql, deactivateAllEnumsSql, getEnumSql, persistEnumSql, updateEnumSql);
		});

	}

	public List<Class<Enum<?>>> findAnnotatedClasses() {
		List<Class<Enum<?>>> annotatedClasses = new ArrayList<>();

		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(PersistedEnum.class));
		provider.findCandidateComponents("nl.smith").forEach(candidateComponent -> {
			try {
				String beanClassName = candidateComponent.getBeanClassName();
				annotatedClasses.add((Class<Enum<?>>) Class.forName(beanClassName));
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException("An unexpected error occurred.", e);
			}
		});

		return annotatedClasses;
	}

	public void updateEnums(Class<Enum<?>> annotatedClass, String createEnumTableSql, String deactivateAllEnums, String getEnumSql, String persistEnumSql, String updateEnumSql) {

		String sql = createEnumTableSql.replaceFirst("\\$\\{enumClassname\\}", annotatedClass.getSimpleName());
		jdbcTemplate.execute(sql);
		LOGGER.info("Created table for enum {} if not existing.", annotatedClass.getCanonicalName());

		sql = deactivateAllEnums.replaceFirst("\\$\\{enumClassname\\}", annotatedClass.getSimpleName());
		jdbcTemplate.execute(sql);
		LOGGER.info("Deactivated entries in table for enum {} if not existing.", annotatedClass.getCanonicalName());

		Arrays.asList(annotatedClass.getEnumConstants()).forEach(constant -> {
			String enumClassname = annotatedClass.getSimpleName();
			String name = ((Enum<?>) constant).name();
			String description = ((AbstractEnum) constant).getDescription();

			String sqlEntry = getEnumSql.replaceFirst("\\$\\{enumClassname\\}", enumClassname).replaceFirst("\\$\\{name\\}", name);

			List<EnumConstant> enumConstants = jdbcTemplate.query(sqlEntry, new RowMapper<EnumConstant>() {

				@Override
				public EnumConstant mapRow(ResultSet result, int rowNum) throws SQLException {
					EnumConstant enumConstant = new EnumConstant(result.getString("name"), result.getString("description"), result.getBoolean("active"));
					return enumConstant;
				}

			});

			switch (enumConstants.size()) {
			case 0:
				// @formatter:off
				sqlEntry = persistEnumSql.replaceFirst("\\$\\{enumClassname\\}", annotatedClass.getSimpleName())
						.replaceFirst("\\$\\{name\\}", name)
						.replaceFirst("\\$\\{description\\}", description);
				// @formatter:on

				jdbcTemplate.execute(sqlEntry);
				LOGGER.info("Created enum {}.{}. Description: {}", enumClassname, name, description);
				break;
			case 1:
				if (!description.equals(enumConstants.get(0).getDescription()) || !enumConstants.get(0).active) {
					// @formatter:off
					sqlEntry = updateEnumSql.replaceFirst("\\$\\{enumClassname\\}", annotatedClass.getSimpleName())
							.replaceFirst("\\$\\{name\\}", name)
							.replaceFirst("\\$\\{description\\}", description);
					// @formatter:on
					jdbcTemplate.execute(sqlEntry);
					LOGGER.info("Updated enum {}.{}. Description: {}", enumClassname, name, description);
				} else {
					LOGGER.info("No Update needed for enum {}.{}. Description: {}", enumClassname, name, description);
				}
				break;
			default:
				break;
			}
		});

	}

	private class EnumConstant implements AbstractEnum {

		private final String name;

		private final String description;

		private final boolean active;

		public EnumConstant(String name, String description, boolean active) {
			this.name = name;
			this.description = description;
			this.active = active;
		}

		public String getName() {
			return name;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String toString() {
			return "EnumConstant [name=" + name + ", description=" + description + "]";
		}

	}

}
