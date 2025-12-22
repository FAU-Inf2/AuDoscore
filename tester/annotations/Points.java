package tester.annotations;

/**
 * Use this annotation to mark a test case method for grading and define its effect.
 * The effect can be positive (bonus) or negative (malus), but not both at the same time.
 */
@java.lang.annotation.Inherited
@java.lang.annotation.Target(java.lang.annotation.ElementType.METHOD)
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@org.junit.jupiter.api.Test
@org.junit.jupiter.api.Timeout(value = 1, threadMode = org.junit.jupiter.api.Timeout.ThreadMode.SEPARATE_THREAD)
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
