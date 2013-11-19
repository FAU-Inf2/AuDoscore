import java.lang.annotation.*;
@Target(java.lang.annotation.ElementType.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface UsageRestriction {
	MustUse[] mustUse() default {};
	MustNotUse[] mustNotUse() default {};
}