package tester.annotations;

import java.lang.annotation.*;

/**
 * The inverse of {@link tester.annotations.Forbidden}. Takes precedence over
 * Forbidden.
 */
@Inherited
@Target(java.lang.annotation.ElementType.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface NotForbidden {
	String[] value();
	Forbidden.Type type() default Forbidden.Type.PREFIX;
}

