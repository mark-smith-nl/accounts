package nl.smith.account.development.validation;

import javax.validation.constraints.NotNull;

public class SimplePojo {

	@NotNull(groups = FieldOne.class, message = "Does not have first name1234")
	private String firstName;

	@NotNull(groups = FieldTwo.class, message = "Does not have last nameeeeeeeeeeeeeeeee")
	private String lastName;

	@NotNull(groups = FieldTwo.class, message = "Does not have an alias")
	private String alias;

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

}
