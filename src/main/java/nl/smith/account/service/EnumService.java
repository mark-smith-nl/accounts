package nl.smith.account.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

import nl.smith.account.annotation.PersistedInTable;
import nl.smith.account.domain.PersistedEnum;
import nl.smith.account.enums.AbstractEnum;
import nl.smith.account.persistence.PersistedEnumMapper;

@Service
public class EnumService {

	private final static Logger LOGGER = LoggerFactory.getLogger(EnumService.class);

	private final PersistedEnumMapper persistedEnumMapper;

	public EnumService(PersistedEnumMapper persistedEnumMapper) {
		this.persistedEnumMapper = persistedEnumMapper;
	}

	// @DependsOn({ "flywayInitializerrrrrr", "flyway" })
	// @PostConstruct
	public void synchronizePersistedEnums() {
		getPersistedEnumClasses().forEach(enumClass -> synchronizePersistedEnum(enumClass));
	}

	public <T extends Class<? extends AbstractEnum>> List<PersistedEnum> getPersistedEnums(T enumClass) {
		return persistedEnumMapper.getPersistedEnums(enumClass.getCanonicalName(), getTableName(enumClass));
	}

	@SuppressWarnings("unchecked")
	protected static List<Class<AbstractEnum>> getPersistedEnumClasses() {
		List<Class<AbstractEnum>> persistedEnumClasses = new ArrayList<>();

		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(PersistedInTable.class));

		provider.findCandidateComponents("nl.smith.account.enums").forEach(candidateComponent -> {
			try {
				String beanClassName = candidateComponent.getBeanClassName();
				persistedEnumClasses.add((Class<AbstractEnum>) Class.forName(beanClassName));
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException("An unexpected error occurred.", e);
			}
		});

		return persistedEnumClasses;
	}

	private void synchronizePersistedEnum(Class<AbstractEnum> enumClass) {
		validateEnumConstants(enumClass);

		String tableName = getTableName(enumClass);

		persistedEnumMapper.disableAllPersistedEnums(tableName);

		final List<String> persistedEnumNames = persistedEnumMapper.getPersistedEnumNames(tableName);

		LOGGER.info("Deactivated all entries in table {} for enum {}.", tableName, enumClass.getCanonicalName());
		LOGGER.info("Deactivated entries in table {} :{}", tableName, String.join(", ", persistedEnumNames));

		AbstractEnum[] enumConstants = enumClass.getEnumConstants();

		String fullyQualifiedClassName = enumClass.getCanonicalName();
		Arrays.asList(enumConstants).forEach(c -> {
			if (persistedEnumNames.contains(c.name())) {
				PersistedEnum persistedEnum = persistedEnumMapper.getPersistedEnumByName(fullyQualifiedClassName, tableName, c.name());
				if (persistedEnum.synchronizePersistedEnum(c)) {
					persistedEnumMapper.updateEnum(tableName, c.name(), c.getDescription(), c.isDefaultValue(), c.isActiveValue());
					LOGGER.info("Updated entry {} in table {} : ", c.name(), tableName);
				} else {
					LOGGER.info("No need to Update entry {} in table {} : ", c.name(), tableName);
				}
			} else {
				persistedEnumMapper.insertEnum(tableName, c.name(), c.getDescription(), c.isDefaultValue(), c.isActiveValue());
				LOGGER.info("Created entry {} in table {} : ", c.name(), tableName);
			}
		});

	}

	private static <T extends Class<? extends AbstractEnum>> String getTableName(T enumClass) {
		PersistedInTable annotation = enumClass.getAnnotation(PersistedInTable.class);

		if (annotation == null) {
			throw new IllegalArgumentException(String.format("The enum class %s is not related to any database table.", enumClass.getCanonicalName()));
		}

		String tableName = annotation.value();
		if (tableName.equals("")) {
			tableName = enumClass.getSimpleName().toLowerCase();
		}

		return tableName;
	}

	/** Method validates that there is only one element that is marked as default. */
	private static void validateEnumConstants(Class<AbstractEnum> annotatedEnumClass) {

		List<AbstractEnum> collect = Arrays.asList(annotatedEnumClass.getEnumConstants()).stream().filter(c -> c.isDefaultValue()).collect(Collectors.toList());

		if (collect.size() > 1) {
			List<String> names = new ArrayList<>();
			collect.forEach(c -> names.add(c.name()));
			String error = String.format("The enum class %s has multiple values set as default (%s)", annotatedEnumClass.getCanonicalName(), String.join(", ", names));
			throw new IllegalStateException(error);
		}
	}

}
