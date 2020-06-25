package tester.annotations;

import java.lang.annotation.*;

/**
 * This annotation defines the secret test class.
 */
@Inherited
@Target(java.lang.annotation.ElementType.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface SecretClass {
}
