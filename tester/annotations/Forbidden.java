package tester.annotations;

import java.lang.annotation.*;

/**
 * This annotation can be used to specify forbidden API fields/methods.
 * Put this annotation to the public test case.
 *
 * @see tester.annotations.NotForbidden
 */
@Inherited
@Target(java.lang.annotation.ElementType.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Forbidden {

	/**
	 * The different types of detecting forbidden fields/methods.
	 */
	enum Type {
		/**
		 * The given strings are prefixes to the forbidden fields/methods.
		 */
		PREFIX,
		/**
		 * The given strings must match the forbidden fields/methods exactly.
		 */
		FIXED,
		/**
		 * The given strings may contain wildcard (asterisk) characters.
		 */
		WILDCARD
	}

	/**
	 * An array of forbidden fields/methods.
	 * The forbidden fields/methods must be specified using their fully qualified names.
	 */
	String[] value();

	/**
	 * The way in which the forbidden fields/methods are detected.
	 * The default is a prefix-based search (i.e., all fields/methods are forbidden
	 * whose prefix is specified as part of this annotation).
	 */
	Type type() default Type.PREFIX;
}
