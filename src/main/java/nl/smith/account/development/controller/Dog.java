package nl.smith.account.development.controller;

public class Dog extends Animal {

	private boolean barks;

	public Dog() {

	}

	public Dog(String name) {
		super(name);
	}

	public Dog(String name, boolean barks) {
		this(name);
		this.barks = barks;
	}

	public boolean isBarks() {
		return barks;
	}

	public void setBarks(boolean barks) {
		this.barks = barks;
	}
}
