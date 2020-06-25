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
	 * The unique identifier for the exercise. This is used to match individual
	 * test methods to exercises.
	 */
	String exID();

	/**
	 * The maximal number of points a student can achieve.
	 */
	double points();

	/**
	 * An optional comment describing the exercise. This is shown in the summary
	 * after the grading is done. If it is not given, the exID will be shown
	 * instead.
	 */
	String comment() default "<n.a.>";
}

