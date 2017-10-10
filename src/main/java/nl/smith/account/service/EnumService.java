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
		String sqlCreateEnumTable = FileCopyUtils.copyToString(inputStreamReader);

		inputStream = this.getClass().getResourceAsStream("getEnumByValue.sql");
		inputStreamReader = new InputStreamReader(inputStream);
		String sqlGetEnumByValue = FileCopyUtils.copyToString(inputStreamReader);

		inputStream = this.getClass().getResourceAsStream("persistEnum.sql");
		inputStreamReader = new InputStreamReader(inputStream);
		String sqlPersistEnum = FileCopyUtils.copyToString(inputStreamReader);

		inputStream = this.getClass().getResourceAsStream("updateEnum.sql");
		inputStreamReader = new InputStreamReader(inputStream);
		String sqlUpdateEnum = FileCopyUtils.copyToString(inputStreamReader);

		// getEnumByValue.sql
		annotatedClasses.forEach(annotatedClass -> {
			createTableIfNotExistsForEnum(sqlCreateEnumTable, annotatedClass);
			updateTableForEnum(annotatedClass, sqlGetEnumByValue, sqlPersistEnum, sqlUpdateEnum);
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
				// TODO: handle exception
			}
		});

		return annotatedClasses;
	}

	private Object createTableIfNotExistsForEnum(String sqlCreateEnumTable, Class<Enum<?>> annotatedClass) {
		LOGGER.info("Create table for enum {} if not existing.", annotatedClass.getCanonicalName());

		String sql = sqlCreateEnumTable.replaceFirst("\\$\\{enumClassname\\}", annotatedClass.getSimpleName());

		jdbcTemplate.execute(sql);

		return null;
	}

	public void updateTableForEnum(Class<Enum<?>> annotatedClass, String sqlGetEnumByValue, String sqlPersistEnum,
			String sqlUpdateEnum) {

		Arrays.asList(annotatedClass.getEnumConstants()).forEach(constant -> {
			String enumClassname = annotatedClass.getSimpleName();
			String name = ((Enum<?>) constant).name();
			String description = ((AbstractEnum) constant).getDescription();

			String sql = sqlGetEnumByValue.replaceFirst("\\$\\{enumClassname\\}", enumClassname);
			sql = sql.replaceFirst("\\$\\{name\\}", name);
			List<EnumConstant> enumConstants = jdbcTemplate.query(sql, new RowMapper<EnumConstant>() {

				@Override
				public EnumConstant mapRow(ResultSet result, int rowNum) throws SQLException {
					EnumConstant enumConstant = new EnumConstant(result.getString("name"),
							result.getString("description"));
					return enumConstant;
				}

			});

			LOGGER.info("Times enum {}.{} found: {}", annotatedClass.getCanonicalName(), name, enumConstants.size());
			switch (enumConstants.size()) {
			case 0:
				sql = sqlPersistEnum.replaceFirst("\\$\\{enumClassname\\}", annotatedClass.getSimpleName());
				sql = sql.replaceFirst("\\$\\{name\\}", name);
				sql = sql.replaceFirst("\\$\\{description\\}", description);

				jdbcTemplate.execute(sql);
				LOGGER.info("Created enum {}.{}. Description: {}", enumClassname, name, description);
				break;
			case 1:
				if (!description.equals(enumConstants.get(0).getDescription())) {
					sql = sqlUpdateEnum.replaceFirst("\\$\\{enumClassname\\}", annotatedClass.getSimpleName());
					sql = sql.replaceFirst("\\$\\{name\\}", name);
					sql = sql.replaceFirst("\\$\\{description\\}", description);

					jdbcTemplate.execute(sql);
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

	private class EnumConstant {

		private final String name;

		private final String description;

		public EnumConstant(String name, String description) {
			this.name = name;
			this.description = description;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		@Override
		public String toString() {
			return "EnumConstant [name=" + name + ", description=" + description + "]";
		}

	}

}
