package tester.annotations;

import java.lang.annotation.*;

// ******************** ANNOTATIONS **************************************** //
@Inherited
@Target(java.lang.annotation.ElementType.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Exercises {
	Ex[] value();
}
