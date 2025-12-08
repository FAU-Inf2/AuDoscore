package tester.tools;

import org.junit.jupiter.api.*;
import org.junit.platform.commons.support.*;
import org.junit.jupiter.api.extension.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.file.Path;
import tester.annotations.InitializeOnce;

public final class InitializeOnceHandler implements TestInstancePostProcessor {
	@Override
	public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
		Class<?> testClass = context.getRequiredTestClass();
		AnnotationSupport.findAnnotatedFields(testClass, InitializeOnce.class).forEach(field -> {
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
					Assertions.fail(String.valueOf(e.getCause()));
				}
			}
		});
	}
}
