package nl.smith.account.enums;

import nl.smith.account.annotation.PersistedInTable;

@PersistedInTable
public enum Currency implements AbstractEnum {

	HFL("Valuta: Gulden (Oud)"),
	EUR("Valuta: Euro", false, false),
	EUR2("Valuta: Euro2", true, true),
	USD("United States Dollar"),
	GBP("Valuta: United Kingdom Pound");

	private String description;

	private boolean defaultValue;

	private boolean activeValue;

	Currency(String description) {
		this(description, false, false);
	}

	Currency(String description, boolean defaultValue, boolean activeValue) {
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
