package nl.smith.account.enums;

import nl.smith.account.annotation.PersistedInTable;

@PersistedInTable
public enum Currency implements AbstractEnum {

	HFL("Valuta: Gulden (Oud)"),
	EUR("Valuta: Euro", true),
	USD("United States Dollar"),
	GBP("Valuta: United Kingdom Pound");

	private String description;

	private boolean defaultValue;

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
