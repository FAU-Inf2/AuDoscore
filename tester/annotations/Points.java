package tester.annotations;

import java.lang.annotation.*;

/**
 * Use this annotation to define the effect of a test method.
 * The effect can be positive (bonus) or negative (malus), but not both at the same time.
 */
@Inherited
@Target(java.lang.annotation.ElementType.METHOD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Points {
	/**
	 * The unique identifier of the exercise this test method belongs to.
	 */
	String exID();

	/**
	 * The relative number of points earned when passing this test.
	 */
	double bonus() default -1;

	/**
	 * The relative number of points lost when failing this test.
	 */
	double malus() default -1;

	/**
	 * An optional informative comment.
	 * If given, it will be used in place of the test method name in the summary.
	 */
	String comment() default "<n.a.>";
}
