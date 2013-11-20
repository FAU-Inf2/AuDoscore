import java.lang.annotation.*;
@Target(java.lang.annotation.ElementType.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface MustNotUse {
	String classname();
	String[] methods();
	String[] notUsable();
	double malus();
	String exID();
	String comment() default "";
	String error() default "";
}
