package tester.annotations;

import java.lang.annotation.*;

@Inherited
@Target(java.lang.annotation.ElementType.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Ex {
	String exID();

	double points();

	String comment() default "<n.a.>";
}

