package nl.smith.account.enums.persisted;

import nl.smith.account.annotation.PersistedInTable;

@PersistedInTable(tableName = "osama")
public enum AccountNumber implements AbstractPersistedEnum {

	R449937763("Priverekening", true);

	private final String description;

	private final boolean defaultValue;

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
