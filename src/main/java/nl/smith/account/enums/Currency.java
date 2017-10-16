package nl.smith.account.enums;

import nl.smith.account.annotation.PersistedEnum;

@PersistedEnum
public enum Currency implements AbstractEnum {

	HFL("Valuta: Gulden (Oud)"),
	EUR("Valuta: Euro", true),
	USD("United States Dollar"),
	GBP("Valuta: United Kingdom Pound");

	private boolean isDefault;

	private String description;

	Currency(String description) {
		this(description, false);
	}

	Currency(String description, boolean isDefault) {
		this.description = description;
		this.isDefault = isDefault;
	}

	@Override
	public boolean isDefault() {
		return isDefault;
	}

	@Override
	public String getDescription() {
		return description;
	}

}
