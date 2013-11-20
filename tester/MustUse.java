import java.lang.annotation.*;
@Target(java.lang.annotation.ElementType.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface MustUse {
	String classname();
	String[] methods();
	String[] usable();
	double malus();
	String exID();
}
