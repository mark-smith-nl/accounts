package nl.smith.account.development.domain;

public abstract class Animal {

	@SuppressWarnings("unused")
	private String className;

	private String name;

	public Animal() {

	}

	public Animal(String name) {
		this.name = name;
	}

	public String getClassName() {
		return this.getClass().getCanonicalName();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
