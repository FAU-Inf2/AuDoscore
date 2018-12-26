package tester.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * Used to mark the possibility of method replacements. This means, that this
 * test is executed twice. The first run uses the student's code as is. In the
 * second run, the given methods are replaced with their cleanroom
 * equivalents. If the test passes one of these runs, the student earns the
 * respective number of points. This annotation applies to a single test
 * method in the secret test class.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Replace{
	/**
	 * The names of the methods to replace. These must be fully qualified names.
	 */
	String[] value();

	/**
	 * A condition to fulfil for replacement to occur. Currently, it is only
	 * possible to check for the presence of certain fields or methods. The
	 * syntax is as follows:
	 * <ul>
	 * <li>Fields: <code>field;&lt;fully qualified name of the
	 *     field&gt;;&lt;required type&gt;</code></li>
	 * <li>Methods: <code>method;&lt;fully qualified name of the
	 *     method&gt;;&lt;required return type&gt;&lt;required type of the first
	 *     parameter&gt;...</code></li>
	 * </ul>
	 */
	String onlyIf() default "";
}
