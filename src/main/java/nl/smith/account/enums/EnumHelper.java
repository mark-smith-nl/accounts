package nl.smith.account.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumHelper {

    private EnumHelper() {
        throw new IllegalAccessError("Constructor can notbe invoced on a utillity class");
    }

    public static <T extends AbstractEnum> T getEnumByName(Class<T> clazz, String name) {
        List<T> collect = Arrays.asList(clazz.getEnumConstants()).stream().filter(c -> ((Enum<?>) c).name().equals(name)).collect(Collectors.toList());

        if (collect.size() > 1) {
            throw new IllegalStateException(String.format("Multiple entries found for enum of type '%s' and name '%s'", clazz.getCanonicalName(), name));
        }

        return (collect.size() == 0) ? null : collect.get(0);
    }

    public static <T extends AbstractEnum> T getEnumByCode(Class<T> clazz, String code) {
        List<T> collect = Arrays.asList(clazz.getEnumConstants()).stream().filter(c -> c.getCode().equals(code)).collect(Collectors.toList());

        if (collect.size() > 1) {
            throw new IllegalStateException(String.format("Multiple entries found for enum of type '%s' and code '%s'", clazz.getCanonicalName(), code));
        }

        return (collect.size() == 0) ? null : collect.get(0);
    }

    public static <T extends AbstractEnum> List<T> getEnumByDescription(Class<T> clazz, String description) {
        List<T> collect = Arrays.asList(clazz.getEnumConstants()).stream().filter(c -> c.getDescription().equals(description)).collect(Collectors.toList());

        return collect;
    }

}
