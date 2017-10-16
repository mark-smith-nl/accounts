package nl.smith.account.enums;

public interface AbstractEnum {

	String name();

	boolean isDefault();

	String getDescription();

	default boolean isActive() {
		return true;
	}
}
