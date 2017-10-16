package nl.smith.account.enums;

import nl.smith.account.annotation.PersistedEnum;

@PersistedEnum
public enum MutationType implements AbstractEnum {

	DEBET("DEBET: Withdraw from account"),
	CREDIT("CREDIT: Add to account");

	private boolean isDefault;

	private String description;

	MutationType(String description) {
		this(description, false);
	}

	MutationType(String description, boolean isDefault) {
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