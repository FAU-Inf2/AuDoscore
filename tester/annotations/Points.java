package tester.annotations;

import java.lang.annotation.*;

@Inherited
@Target(java.lang.annotation.ElementType.METHOD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Points {
	String exID();

	double bonus();
	double malus();

	String comment() default "<n.a.>";
}

