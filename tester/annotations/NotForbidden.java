package tester.annotations;

/**
 * The inverse of {@link tester.annotations.Forbidden}.
 * Takes precedence over {@link tester.annotations.Forbidden}.
 *
 * @see tester.annotations.Forbidden
 */
@java.lang.annotation.Inherited
@java.lang.annotation.Target(java.lang.annotation.ElementType.TYPE)
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface NotForbidden {
	String[] value();

	Forbidden.Type type() default Forbidden.Type.PREFIX;
}
