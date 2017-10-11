package nl.smith.account.enums;

import nl.smith.account.annotation.PersistedEnum;

@PersistedEnum
public enum Currency implements AbstractEnum {

	HFL("Valuta: Gulden (Oud)"),
	EUR("Valuta: Euro"),
	GBP("Valuta: United Kingdom Pound");

	private String description;

	Currency(String description) {
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

}
