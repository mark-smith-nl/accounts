package nl.smith.account.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
		List<Class<Enum<?>>> annotatedClasses = findAnnotatedEnumClasses();

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

	public List<Class<Enum<?>>> findAnnotatedEnumClasses() {
		List<Class<Enum<?>>> annotatedEnumClasses = new ArrayList<>();

		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(PersistedEnum.class));
		provider.findCandidateComponents("nl.smith").forEach(candidateComponent -> {
			try {
				String beanClassName = candidateComponent.getBeanClassName();
				annotatedEnumClasses.add((Class<Enum<?>>) Class.forName(beanClassName));
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException("An unexpected error occurred.", e);
			}
		});

		return annotatedEnumClasses;
	}

	public void updateEnums(Class<Enum<?>> annotatedEnumClass, String createEnumTableSql, String deactivateAllEnums, String getEnumSql, String persistEnumSql,
			String updateEnumSql) {

		String sql = createEnumTableSql.replaceAll("\\$\\{enumClassname\\}", annotatedEnumClass.getSimpleName());
		jdbcTemplate.execute(sql);
		LOGGER.info("Created table for enum {} if not existing.", annotatedEnumClass.getCanonicalName());

		sql = deactivateAllEnums.replaceAll("\\$\\{enumClassname\\}", annotatedEnumClass.getSimpleName());
		jdbcTemplate.execute(sql);
		LOGGER.info("Deactivated entries in table for enum {} if not existing.", annotatedEnumClass.getCanonicalName());

		// validateEnumConstants(annotatedEnumClass);

		Arrays.asList(annotatedEnumClass.getEnumConstants()).forEach(constant -> {
			String enumClassname = annotatedEnumClass.getSimpleName();
			String name = ((Enum<?>) constant).name();
			boolean isDefault = ((AbstractEnum) constant).isDefault();
			String description = ((AbstractEnum) constant).getDescription();

			String sqlEntry = getEnumSql.replaceAll("\\$\\{enumClassname\\}", enumClassname).replaceAll("\\$\\{name\\}", name);

			List<PersistedEnumConstant> persistedEnumConstants = jdbcTemplate.query(sqlEntry, new RowMapper<PersistedEnumConstant>() {

				@Override
				public PersistedEnumConstant mapRow(ResultSet result, int rowNum) throws SQLException {
					PersistedEnumConstant persistedEnumConstant = new PersistedEnumConstant(result.getString("name"), result.getBoolean("isdefault"),
							result.getString("description"), result.getBoolean("active"));
					return persistedEnumConstant;
				}

			});

			switch (persistedEnumConstants.size()) {
			case 0:
				// @formatter:off
				sqlEntry = persistEnumSql.replaceAll("\\$\\{enumClassname\\}", annotatedEnumClass.getSimpleName())
						.replaceAll("\\$\\{name\\}", name)
						.replaceAll("\\$\\{isdefault\\}", String.valueOf(isDefault))
						.replaceAll("\\$\\{description\\}", description);
				// @formatter:on

				jdbcTemplate.execute(sqlEntry);
				LOGGER.info("Created enum {}.{}. Description: {}", enumClassname, name, description);
				break;
			case 1:
				PersistedEnumConstant persistedEnumConstant = persistedEnumConstants.get(0);
				if (isDefault != persistedEnumConstant.isDefault() || !description.equals(persistedEnumConstant.getDescription()) || !persistedEnumConstant.isActive()) {
					// @formatter:off
					sqlEntry = updateEnumSql.replaceAll("\\$\\{enumClassname\\}", annotatedEnumClass.getSimpleName())
							.replaceAll("\\$\\{name\\}", name)
							.replaceAll("\\$\\{isdefault\\}", String.valueOf(isDefault))
							.replaceAll("\\$\\{description\\}", description);
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

	private void validateEnumConstants(Class<Enum<?>> annotatedEnumClass) {

		List<Enum<?>> collect = Arrays.asList(annotatedEnumClass.getEnumConstants()).stream().filter(c -> ((AbstractEnum) c).isDefault()).collect(Collectors.toList());

		if (collect.size() > 1) {
			List<String> names = new ArrayList<>();
			collect.forEach(c -> names.add(c.name()));
			String error = String.format("The enum class %s has multiple values set as default (%s)", annotatedEnumClass.getCanonicalName(), String.join(", ", names));
			throw new IllegalStateException(error);
		}
	}

	private class PersistedEnumConstant implements AbstractEnum {

		private final String name;

		private boolean isDefault;

		private final String description;

		private final boolean active;

		public PersistedEnumConstant(String name, boolean isDefault, String description, boolean active) {
			this.name = name;
			this.description = description;
			this.isDefault = isDefault;
			this.active = active;
		}

		@Override
		public String name() {
			return name;
		}

		@Override
		public boolean isDefault() {
			return isDefault;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public boolean isActive() {
			return active;
		}

		@Override
		public String toString() {
			return "PersistedEnumConstant [name=" + name + ", isDefault=" + isDefault + ", description=" + description + ", active=" + active + "]";
		}

	}

}
