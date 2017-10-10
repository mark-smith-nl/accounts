package nl.smith.account.enums;

import nl.smith.account.annotation.PersistedEnum;

@PersistedEnum
public enum Currency implements AbstractEnum {

	HFL("HFL", "Valuta: Gulden (Oud)"), EUR("EUR", "Valuta: Euro"), GBP("GBP", "Valuta: United Kingdom Pound");

	private String code;

	private String description;

	Currency(String code, String description) {
		this.code = code;
		this.description = description;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return this.name() + "{" + "code: '" + code + "', " + "description: '" + description + "'" + "}";
	}

}
