package tester.annotations;

import java.lang.annotation.*;

/**
 * A single exercise definition.
 */
@Inherited
@Target(java.lang.annotation.ElementType.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
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
