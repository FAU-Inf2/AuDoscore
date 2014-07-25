package tester.annotations;

import java.lang.annotation.*;

@Inherited
@Target(java.lang.annotation.ElementType.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface NotForbidden {
	String[] value();
}

