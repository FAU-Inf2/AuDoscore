package tester.annotations;

import java.lang.annotation.*;

@Inherited
@Target(java.lang.annotation.ElementType.METHOD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Points {
	String exID();

	double bonus() default -1;
	double malus() default -1;

	String bonusComment() default "<n.a.>";
	String malusComment() default "<n.a.>";
}

