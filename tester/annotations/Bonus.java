package tester.annotations;

import java.lang.annotation.*;

@Inherited
@Target(java.lang.annotation.ElementType.METHOD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Bonus {
	String exID();

	double bonus();

	String comment() default "<n.a.>";
}

