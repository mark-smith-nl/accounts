package nl.smith.account.persistence;

import java.util.Set;

import org.apache.ibatis.annotations.Param;

import nl.smith.account.domain.PersistedEnum;

public interface PersistedEnumMapper {

	void disablePersistedEnum(@Param("tableName") String tableName, @Param("name") String name);

	Set<PersistedEnum> getPersistedEnums(@Param("tableName") String tableName);

	void insertEnum(@Param("tableName") String tableName, @Param("name") String name, @Param("description") String description, @Param("defaultValue") boolean defaultValue);

	void updateEnum(@Param("tableName") String tableName, @Param("name") String name, @Param("description") String description, @Param("defaultValue") boolean defaultValue);
}
