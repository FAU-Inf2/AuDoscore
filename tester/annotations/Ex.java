package tester.annotations;

/**
 * A single exercise definition.
 */
@java.lang.annotation.Inherited
@java.lang.annotation.Target(java.lang.annotation.ElementType.TYPE)
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Ex {
	/**
	 * The unique identifier for the exercise. This is used to match individual test methods to exercises.
	 */
	String exID();

	/**
	 * The maximal number of points a student can achieve.
	 */
	double points();
}
