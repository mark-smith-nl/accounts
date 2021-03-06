package nl.smith.account.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

import nl.smith.account.annotation.PersistedInTable;
import nl.smith.account.domain.PersistedEnum;
import nl.smith.account.enums.persisted.AbstractPersistedEnum;
import nl.smith.account.persistence.PersistedEnumMapper;

/** Service to synchronize enums of type in the database.
 * This service should run after the database has been updated by flyway.
 */
@DependsOn("flywayInitializer")
@Service
public class EnumService {

	private final static Logger LOGGER = LoggerFactory.getLogger(EnumService.class);

	private final PersistedEnumMapper persistedEnumMapper;

	public EnumService(PersistedEnumMapper persistedEnumMapper) {
		this.persistedEnumMapper = persistedEnumMapper;
	}

	@PostConstruct
	public void synchronizePersistedEnums() {
		getPersistedEnumClasses().forEach(enumClass -> synchronizePersistedEnum(enumClass));
	}

	@SuppressWarnings("unchecked")
	protected static List<Class<AbstractPersistedEnum>> getPersistedEnumClasses() {
		List<Class<AbstractPersistedEnum>> persistedEnumClasses = new ArrayList<>();

		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(PersistedInTable.class));

		provider.findCandidateComponents("nl.smith.account.enums").forEach(candidateComponent -> {
			try {
				String beanClassName = candidateComponent.getBeanClassName();
				persistedEnumClasses.add((Class<AbstractPersistedEnum>) Class.forName(beanClassName));
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException("An unexpected error occurred.", e);
			}
		});

		return persistedEnumClasses;
	}

	private void synchronizePersistedEnum(Class<AbstractPersistedEnum> enumClass) {
		validateEnumConstants(enumClass);

		String tableName = getTableName(enumClass);
		String fullyQualifiedClassName = enumClass.getCanonicalName();

		Map<String, PersistedEnum> persistedValueMap = persistedEnumMapper.getPersistedEnums(tableName).stream()
				.collect(Collectors.toMap(PersistedEnum::name, Function.identity()));
		Map<String, AbstractPersistedEnum> enumValueMap = Stream.of(enumClass.getEnumConstants()).collect(Collectors.toMap(AbstractPersistedEnum::name, Function.identity()));
		Set<String> allEnumNames = new HashSet<>();

		allEnumNames.addAll(persistedValueMap.keySet());
		allEnumNames.addAll(enumValueMap.keySet());

		allEnumNames.forEach(name -> {
			PersistedEnum persistedValue = persistedValueMap.get(name);
			AbstractPersistedEnum enumValue = enumValueMap.get(name);
			if (persistedValue == null) {
				persistedEnumMapper.insertEnum(tableName, enumValue.name(), enumValue.getDescription(), enumValue.isDefaultValue());
				LOGGER.info("Created entry {} in table({}).", enumValue.name(), tableName);
			} else {
				if (enumValue == null) {
					if (persistedValue.isDefaultValue() || persistedValue.isActiveValue()) {
						persistedEnumMapper.disablePersistedEnum(tableName, name);
						LOGGER.info("Disabled unused entry {} in table({} ).", name, tableName);
					} else {
						LOGGER.info("Unused entry {} already disabled in table({} ).", name, tableName);
					}
				} else {
					if (persistedValue.isSynchronized(fullyQualifiedClassName, enumValue)) {
						LOGGER.info("No need to Update entry {} in table({}).", name, tableName);
					} else {
						persistedEnumMapper.updateEnum(tableName, enumValue.name(), enumValue.getDescription(), enumValue.isDefaultValue());
						LOGGER.info("Updated entry {} in table({}).", name, tableName);
					}
				}
			}
		});
	}

	/** Protected for test purposes */
	protected static <T extends AbstractPersistedEnum> String getTableName(Class<T> enumClass) {
		PersistedInTable annotation = enumClass.getAnnotation(PersistedInTable.class);

		if (annotation == null) {
			throw new IllegalArgumentException(String.format("The enum class %s is not related to any database table.", enumClass.getCanonicalName()));
		}

		String tableName = annotation.tableName();
		if (tableName.equals("")) {
			tableName = enumClass.getSimpleName().toLowerCase();
		}

		return tableName;
	}

	/** Method validates that there is only one element that is marked as default. */
	private static void validateEnumConstants(Class<AbstractPersistedEnum> annotatedEnumClass) {

		List<AbstractPersistedEnum> collect = List.of(annotatedEnumClass.getEnumConstants()).stream().filter(c -> c.isDefaultValue()).collect(Collectors.toList());

		if (collect.size() > 1) {
			List<String> names = new ArrayList<>();
			collect.forEach(c -> names.add(c.name()));
			String error = String.format("The enum class %s has multiple values set as default (%s)", annotatedEnumClass.getCanonicalName(), String.join(", ", names));
			throw new IllegalStateException(error);
		}
	}

}
