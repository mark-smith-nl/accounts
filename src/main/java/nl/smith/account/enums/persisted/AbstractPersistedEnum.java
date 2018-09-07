package nl.smith.account.enums.persisted;

import nl.smith.account.enums.AbstractEnum;

public interface AbstractPersistedEnum extends AbstractEnum {

	public String name();

	boolean isDefaultValue();

	default public String asString() {
		return String.format("%s.%s[%s, %b]", getClass().getSimpleName(), name(), getDescription(), isDefaultValue());
	}

}
