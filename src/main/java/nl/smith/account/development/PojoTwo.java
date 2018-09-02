package nl.smith.account.development;

@Deprecated
public class PojoTwo {

	private static int counter;

	private final String name;

	private final int age;

	private final int index;

	public PojoTwo(String name, int age) {
		this.name = name;
		this.age = age;
		index = counter++;
		System.out.println("===>     Created: " + this);
	}

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public String toString() {
		return String.format("PojoOne [name=%s, age=%s, index=%s]", name, age, index);
	}

}
