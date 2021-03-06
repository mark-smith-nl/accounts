package nl.smith.account.enums;

import java.util.List;
import java.util.stream.Collectors;

public interface AbstractEnum {

	String getDescription();

	public static <T extends AbstractEnum> T getEnumByName(Class<T> clazz, String name) {
		List<T> collect = List.of(clazz.getEnumConstants()).stream().filter(c -> ((Enum<?>) c).name().equals(name)).collect(Collectors.toList());

		if (collect.size() > 1) {
			throw new IllegalStateException(String.format("Multiple entries found for enum of type '%s' and name '%s'", clazz.getCanonicalName(), name));
		}

		return (collect.size() == 0) ? null : collect.get(0);
	}

}
