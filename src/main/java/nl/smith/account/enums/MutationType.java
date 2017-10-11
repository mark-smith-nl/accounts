package nl.smith.account.enums;

import nl.smith.account.annotation.PersistedEnum;

@PersistedEnum
public enum MutationType implements AbstractEnum {

	DEBET("DEBET: Withdraw from account"),
	CREDIT("CREDIT: Add to account");

	private String description;

	MutationType(String description) {
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

}
