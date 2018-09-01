package nl.smith.account.enums;

import nl.smith.account.annotation.PersistedInTable;

@PersistedInTable
public enum MutationType implements AbstractEnum {

	DEBET("DEBET: Withdraw from account"),
	CREDIT("CREDIT: Add to account");

	private String description;

	private boolean defaultValue;

	MutationType(String description) {
		this(description, false);
	}

	MutationType(String description, boolean defaultValue) {
		this.description = description;
		this.defaultValue = defaultValue;
	}

	@Override
	public boolean isDefaultValue() {
		return defaultValue;
	}

	@Override
	public String getDescription() {
		return description;
	}

}