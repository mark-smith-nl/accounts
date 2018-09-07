package nl.smith.account.domain;

import nl.smith.account.enums.persisted.AbstractPersistedEnum;

/** <p>An enum value (see: {@link AbstractPersistedEnum}) as persisted in the database.
 * 
 * <p>The property active is set to false in case the actual enum does not support the value.
 * <p>Since database foreign key constraints could exist the value in the database is deactivated. */
public class PersistedEnum implements AbstractPersistedEnum {

	private String name;

	private String description;

	private boolean defaultValue;

	private boolean activeValue;

	@Override
	public String name() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public boolean isDefaultValue() {
		return defaultValue;
	}

	public boolean isActiveValue() {
		return activeValue;
	}

	public boolean isSynchronized(String fullyQualifiedClassName, AbstractPersistedEnum abstractPersistedEnum) {
		if (this == abstractPersistedEnum || !fullyQualifiedClassName.equals(abstractPersistedEnum.getClass().getCanonicalName())) {
			return true;
		}

		return name.equals(abstractPersistedEnum.name()) && description.equals(abstractPersistedEnum.getDescription()) && defaultValue == abstractPersistedEnum.isDefaultValue()
				&& activeValue == true;
	}

	@Override
	public String toString() {
		return String.format("PersistedEnum [name=%s, description=%s, defaultValue=%s, activeValue=%s]", name, description, defaultValue, activeValue);
	}

}
