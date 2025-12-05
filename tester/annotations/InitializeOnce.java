package tester.annotations;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotatedFields;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.*;
import java.io.File;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;

/**
 * This annotation can be used to perform initialization tasks in the secret test exactly once.
 * The field annotated with this annotation is initialized with the result of the method whose name is an argument to the annotation.
 * The method is called once and its result is cached for subsequent initializations.
 */
@java.lang.annotation.Inherited
@java.lang.annotation.Target(java.lang.annotation.ElementType.FIELD)
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@org.junit.jupiter.api.extension.ExtendWith(tester.annotations.InitializeOnceHandler.class)
public @interface InitializeOnce {
	/**
	 * The name of the method that is used for the initialization.
	 */
	String value();
}

class InitializeOnceHandler implements TestInstancePostProcessor {
	@Override
	public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
		Class<?> testClass = context.getRequiredTestClass();
		findAnnotatedFields(testClass, InitializeOnce.class).forEach(field -> {
			field.setAccessible(true);
			// check if the result is already computed:
			final File initFile = Path.of("initializeOnce", testClass.getCanonicalName() + "-" + field.getName() + ".tmp").toFile();
			initFile.getParentFile().mkdirs(); // create if not yet existing
			boolean inStorage = initFile.exists();
			if (inStorage) {
				// the result has been computed, just restore it:
				try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(initFile))) {
					field.set(testInstance, in.readObject());
				} catch (final IOException | ClassNotFoundException | IllegalAccessException e) {
					inStorage = false;
				}
			}
			if (!inStorage) {
				// the result must be computed, stored in the field, and saved in initFile:
				try {
					final InitializeOnce initOnce = field.getAnnotation(InitializeOnce.class);
					Method initMethod = testClass.getDeclaredMethod(initOnce.value());
					initMethod.setAccessible(true);
					final Object result = initMethod.invoke(testInstance);
					field.set(testInstance, result);
					try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(initFile))) {
						out.writeObject(result);
					} catch (final IOException e) {
						initFile.delete(); // try to clean up
					}
				} catch (final NoSuchMethodException | IllegalAccessException e) { // should be checked by CheckAnnotations resp. not happen
					throw new IllegalStateException(e);
				} catch (final InvocationTargetException e) { // might be an exception in student code, so make test fail
					fail(String.valueOf(e.getCause()));
				}
			}
		});
	}
}