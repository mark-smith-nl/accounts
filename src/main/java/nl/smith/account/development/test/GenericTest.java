package nl.smith.account.development.test;

import java.util.ArrayList;
import java.util.List;

//
public class GenericTest {

	private static class Builder {

		private static Builder getBuilder() {
			return new Builder();
		}

		private StepOne getStepOne() {
			return new StepOne();
		}

		private class StepOne {
			List<String> getStringList() {
				return new ArrayList<String>();
			}
		}
	}

	public static void main(String[] args) {
		Builder.getBuilder().getStepOne().getStringList();

	}
}
