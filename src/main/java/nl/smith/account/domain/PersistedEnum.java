package nl.smith.account.domain;

import nl.smith.account.enums.AbstractEnum;

public class PersistedEnum implements AbstractEnum {

	private String fullyQualifiedClassName;

	private String name;

	private String description;

	private boolean defaultValue;

	private boolean activeValue;

	public String getFullyQualifiedClassName() {
		return fullyQualifiedClassName;
	}

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

	@Override
	public boolean isActiveValue() {
		return activeValue;
	}

	public boolean synchronizePersistedEnum(AbstractEnum abstractEnum) {
		if (this == abstractEnum || !fullyQualifiedClassName.equals(abstractEnum.getClass().getCanonicalName())) {
			return false;
		}

		return !(name.equals(abstractEnum.name()) && defaultValue == abstractEnum.isDefaultValue() && activeValue == abstractEnum.isActiveValue());
	}

	@Override
	public String toString() {
		return String.format("PersistedEnum [fullyQualifiedClassName=%s, name=%s, description=%s, defaultValue=%s, activeValue=%s]", fullyQualifiedClassName, name, description,
				defaultValue, activeValue);
	}

}
