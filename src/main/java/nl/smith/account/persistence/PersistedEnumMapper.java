package nl.smith.account.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import nl.smith.account.domain.PersistedEnum;

public interface PersistedEnumMapper {

	void disableAllPersistedEnums(@Param("tableName") String tableName);

	List<String> getPersistedEnumNames(@Param("tableName") String tableName);

	List<PersistedEnum> getPersistedEnums(@Param("fullyQualifiedClassName") String fullyQualifiedClassName, @Param("tableName") String tableName);

	PersistedEnum getPersistedEnumByName(@Param("fullyQualifiedClassName") String fullyQualifiedClassName, @Param("tableName") String tableName, @Param("name") String name);

	void insertEnum(@Param("tableName") String tableName, @Param("name") String name, @Param("description") String description, @Param("defaultValue") boolean defaultValue,
			@Param("activeValue") boolean activeValue);

	void updateEnum(@Param("tableName") String tableName, @Param("name") String name, @Param("description") String description, @Param("defaultValue") boolean defaultValue,
			@Param("activeValue") boolean activeValue);
}
