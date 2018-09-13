package nl.smith.account.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import nl.smith.account.enums.persisted.AbstractPersistedEnum;

/** Maps enums of type {@link AbstractPersistedEnum} to a database table 
 * If no tablename is defined the lowercase simple class name is used as a tableName.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PersistedInTable {
	String tableName() default "";
}
