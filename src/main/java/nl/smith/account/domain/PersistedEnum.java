package nl.smith.account.domain;

import nl.smith.account.enums.AbstractEnum;

public class PersistedEnum implements AbstractEnum {

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

	public boolean isSynchronized(String fullyQualifiedClassName, AbstractEnum abstractEnum) {
		if (this == abstractEnum || !fullyQualifiedClassName.equals(abstractEnum.getClass().getCanonicalName())) {
			return true;
		}

		return name.equals(abstractEnum.name()) && description.equals(abstractEnum.getDescription()) && defaultValue == abstractEnum.isDefaultValue() && activeValue == true;
	}

	@Override
	public String toString() {
		return String.format("PersistedEnum [name=%s, description=%s, defaultValue=%s, activeValue=%s]", name, description, defaultValue, activeValue);
	}

}
