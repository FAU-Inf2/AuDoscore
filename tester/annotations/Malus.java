package tester.annotations;

import java.lang.annotation.*;

@Inherited
@Target(java.lang.annotation.ElementType.METHOD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Deprecated
public @interface Malus {
	String exID();

	double malus();

	String comment() default "<n.a.>";
}

