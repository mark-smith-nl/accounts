package nl.smith.account.development;

import javax.validation.constraints.NotEmpty;

public class Pojo extends RawPojo {

	@Override
	@NotEmpty(message = "A firstname is required")
	public String getFirstName() {
		return super.getFirstName();
	}

	@Override
	@NotEmpty(message = "A lastname is required")
	public String getLastName() {
		return super.getLastName();
	}

}
