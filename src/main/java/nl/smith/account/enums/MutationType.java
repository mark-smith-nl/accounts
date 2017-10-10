package nl.smith.account.enums;

import nl.smith.account.annotation.PersistedEnum;

@PersistedEnum
public enum MutationType implements AbstractEnum {

	DEBET("DEBET", "DEBET: Withdraw from account"),
	CREDIT("CREDIT", "CREDIT: Add to account");

    private String code;

    private String description;

    MutationType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public String toString() {
        return this.name() + "{" + "code: '" + code + "', " + "description: '" + description + "'" + "}";
    }

}
