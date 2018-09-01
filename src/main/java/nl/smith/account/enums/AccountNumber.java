package nl.smith.account.enums;

import nl.smith.account.annotation.PersistedInTable;

@PersistedInTable()
public enum AccountNumber implements AbstractEnum {

	R449937763("Priverekening", true, true);

	private String description;

	private boolean defaultValue;

	private boolean activeValue;

	AccountNumber(String description) {
		this(description, false, false);
	}

	AccountNumber(String description, boolean defaultValue, boolean activeValue) {
		this.description = description;
		this.defaultValue = defaultValue;
		this.activeValue = activeValue;
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

}
