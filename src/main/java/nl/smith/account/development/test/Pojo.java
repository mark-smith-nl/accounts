package nl.smith.account.development.test;

import java.lang.reflect.InvocationTargetException;

import javax.validation.constraints.NotNull;

public class Pojo {

	@NotNull(groups = { ValidateRawPojo.class }, message = "Field one is required")
	private String fieldOne;

	@NotNull(groups = { ValidateRawPojo.class }, message = "Field two is required")
	private String fieldTwo;

	@NotNull(groups = { ValidateRawPojo.class }, message = "Field three is required")
	private String fieldThree;

	@NotNull(groups = { ValidateRawPojo.class }, message = "Field four is required")
	private String fieldFour;

	@NotNull(groups = { ValidateRawPojo.class }, message = "Field five is required")
	private String fieldFive;

	@NotNull(groups = { ValidateCompletePojo.class }, message = "Field six is required")
	private String fieldSix;

	@NotNull(groups = { ValidateCompletePojo.class }, message = "Field seven is required")
	private String fieldSeven;

	private Pojo() {

	}

	public String getFieldOne() {
		return fieldOne;
	}

	public String getFieldTwo() {
		return fieldTwo;
	}

	public String getFieldThree() {
		return fieldThree;
	}

	public String getFieldFour() {
		return fieldFour;
	}

	public String getFieldFive() {
		return fieldFive;
	}

	public String getFieldSix() {
		return fieldSix;
	}

	public String getFieldSeven() {
		return fieldSeven;
	}

	interface ValidateCompletePojo {
	}

	interface ValidateRawPojo {
	}

	interface StepSetLastField {
	}

	public static class PojoBuilder<T extends StepSetLastField> {

		private final Pojo pojo = new Pojo();

		private T stepSetFieldFive;

		private PojoBuilder() {

		}

		public static <T extends StepSetLastField> PojoBuilder<T> getBuilder(Class<T> clazz) {
			try {
				PojoBuilder<T> pojoBuilder = new PojoBuilder<T>();
				pojoBuilder.stepSetFieldFive = clazz.getConstructor(new Class[] { PojoBuilder.class }).newInstance(new Object[] { pojoBuilder });
				return pojoBuilder;
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new IllegalStateException();
			}
		}

		public StepSetFieldTwo setFieldOne(String value) {
			pojo.fieldOne = value;
			return new StepSetFieldTwo();
		}

		public class StepSetFieldTwo {
			public StepSetFieldThree setFieldTwo(String value) {
				pojo.fieldTwo = value;
				return new StepSetFieldThree();
			}
		}

		public class StepSetFieldThree {
			public StepSetFieldFour setFieldThree(String value) {
				pojo.fieldThree = value;
				return new StepSetFieldFour();
			}
		}

		public class StepSetFieldFour {
			public T setFieldFour(String value) {
				pojo.fieldFour = value;
				return stepSetFieldFive;
			}
		}

		public class RawPojo implements StepSetLastField {
			public StepGetPojo setFieldFive(String value) {
				pojo.fieldFive = value;
				return new StepGetPojo();
			}
		}

		public class CompletePojo implements StepSetLastField {
			public StepGetPojo setFieldFive123(String value) {
				pojo.fieldFive = value;
				return new StepGetPojo();
			}
		}

		public class StepGetPojo {
			public Pojo get() {
				return pojo;
			}
		}
	}

}
