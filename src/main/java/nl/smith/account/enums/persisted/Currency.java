package nl.smith.account.enums.persisted;

import nl.smith.account.annotation.PersistedInTable;

@PersistedInTable
public enum Currency implements AbstractPersistedEnum {

	HFL("Valuta: Gulden (Oud)"),
	EUR("Valuta: Euro", true),
	USD("United States Dollar"),
	GBP("Valuta: United Kingdom Pound");

	private final String description;

	private final boolean defaultValue;

	Currency(String description) {
		this(description, false);
	}

	Currency(String description, boolean defaultValue) {
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
