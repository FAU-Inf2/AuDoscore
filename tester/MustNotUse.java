import java.lang.annotation.*;
@Target(java.lang.annotation.ElementType.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface MustNotUse {
	String classname();
	String[] methods();
	String[] usable();
	int malus();
	String exID();
}
