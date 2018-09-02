package nl.smith.account.enums;

import nl.smith.account.annotation.PersistedInTable;

@PersistedInTable()
public enum AccountNumber implements AbstractEnum {

	R449937763("Priverekening", true);

	private String description;

	private boolean defaultValue;

	AccountNumber(String description) {
		this(description, false);
	}

	AccountNumber(String description, boolean defaultValue) {
		this.description = description;
		this.defaultValue = defaultValue;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public boolean isDefaultValue() {
		return defaultValue;
	}

}
