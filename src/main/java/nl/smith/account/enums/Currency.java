package nl.smith.account.enums;

public enum Currency implements AbstractEnum {

    EUR("EUR", "Valuta: Euro");

    private String code;

    private String description;

    Currency(String code, String description) {
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
