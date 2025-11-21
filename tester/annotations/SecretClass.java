package tester.annotations;

/**
 * This annotation marks the secret test class.
 */
@java.lang.annotation.Inherited
@java.lang.annotation.Target(java.lang.annotation.ElementType.TYPE)
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@org.junit.jupiter.api.extension.ExtendWith(tester.tools.JUnitWithPoints.class)
public @interface SecretClass {
}
