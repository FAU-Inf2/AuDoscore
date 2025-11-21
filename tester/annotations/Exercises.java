package tester.annotations;

/**
 * Defines the exercises. Use this annotation to annotate the public test class.
 */
@java.lang.annotation.Inherited
@java.lang.annotation.Target(java.lang.annotation.ElementType.TYPE)
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@org.junit.jupiter.api.extension.ExtendWith(tester.tools.JUnitWithPoints.class)
public @interface Exercises {
	/**
	 * An array of exercise definitions. You may use multiple definitions if the tests are used for multiple exercises.
	 */
	Ex[] value();
}
