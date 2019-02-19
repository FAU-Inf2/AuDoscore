package tester.annotations;

import java.lang.annotation.*;

/**
 * This annotation can be used to perform some initialization task once in the
 * secret test. A field can be annotated with this annotation. The field is
 * initialized with the result of the method whose name is an argument to the
 * annotation. The method is called once and its result is cached for
 * subsequent initializations.
 */
@Target(java.lang.annotation.ElementType.FIELD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface InitializeOnce {
	/**
	 * The name of the method that is used for the initialization.
	 */
	String value();
}

