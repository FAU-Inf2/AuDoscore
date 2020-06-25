package tester.annotations;

import java.lang.annotation.*;

/**
 * A test annotated with this annotation enforces a consistent interface. It
 * is possible to specify fields, methods, or classes by their respective
 * fully qualified name. If fields or methods are given, a student submission
 * has to contain definitions for these public fields or methods that are
 * equivalent to the definitions in the cleanroom code. If a class is given,
 * the respective student's class must contain public fields and methods
 * matching the definitions in the cleanroom class.
 */
@Inherited
@Target(java.lang.annotation.ElementType.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface CompareInterface {
	/**
	 * An array containing the field names, method names, or class names that
	 * are used during comparison.
	 */
	String[] value();
}
