package tester.tools;

import org.junit.jupiter.api.*;
import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.TimeUnit;
import tester.annotations.*;

public class CheckAnnotation {
	public static final int MAX_TIMEOUT_MS = 60_000;
	private static final String cwd = System.getProperty("user.dir");

	static void main(String[] args) {
		try (URLClassLoader unitLoader = new URLClassLoader(new URL[]{new File(cwd, "junit").toURI().toURL()})) {
			Class<?> testClass;
			try {
				testClass = unitLoader.loadClass(args[0]);
			} catch (ClassNotFoundException classNotFoundException) {
				throw new IllegalArgumentException("ERROR - Test class [" + args[0] + "] does not exist");
			}
			checkAnnotations(testClass);
		} catch (IOException malformedURLException) {
			throw new Error("Error " + malformedURLException.getMessage());
		}
	}

	// checks (almost) all annotation conditions
	private static void checkAnnotations(Class<?> testClass) {
		// check annotations on class level
		// check Exercises/Ex annotations in public test if provided
		Optional<Exercises> exercisesAnnotation = JUnitWithPoints.getExercisesAnnotation(testClass);
		if (exercisesAnnotation.isEmpty() || exercisesAnnotation.get().value().length == 0) {
			throw new AnnotationFormatError("ERROR - did not find valid @Exercises declaration: [" + testClass.getName() + "]");
		}
		final HashMap<String, Ex> exerciseHashMap = new HashMap<>();
		for (Ex exercise : exercisesAnnotation.get().value()) {
			if (exercise.exID().trim().isEmpty()) {
				throw new AnnotationFormatError("ERROR - found @Exercises annotation with empty exercise name and following points: [" + exercise.points() + "]");
			} else if (exercise.points() <= 0) {
				throw new AnnotationFormatError("ERROR - found @Exercises annotation with illegal points value: [" + exercise.exID() + "]");
			} else if (exerciseHashMap.containsKey(exercise.exID())) {
				throw new AnnotationFormatError("ERROR - found @Exercises annotation with duplicate exercise: [" + exercise.exID() + "]");
			} else {
				exerciseHashMap.put(exercise.exID(), exercise);
			}
		}
		SecretClass secretClassAnnotation = testClass.getAnnotation(SecretClass.class);
		boolean isSecretClass = secretClassAnnotation != null;
		// check if there are methods to compare with cleanroom counterparts
		CompareInterface compareInterfaceAnnotation = testClass.getAnnotation(CompareInterface.class);
		if (compareInterfaceAnnotation != null) {
			for (String arg : compareInterfaceAnnotation.value()) {
				if (arg.contains(".")) {
					String[] parts = arg.split("\\.");
					if (parts.length != 2) {
						throw new IllegalArgumentException("ERROR - @CompareInterface must look like this: Class, Class.Method or Class.Field, found: " + arg);
					}
					// first part is class name, second part method or field
					Class<?> cleanroomClass = getCleanroomClass(parts[0]);
					// TODO avoid reloading same class
					if (getMethod(cleanroomClass, parts[1]) == null) {
						// method does not exist check Field
						try {
							cleanroomClass.getField(parts[1]);
						} catch (NoSuchFieldException noSuchFieldException) {
							throw new IllegalArgumentException("ERROR - " + arg + " specified in @CompareInterface could not be found in cleanroom");
						}
					}
				} else {
					// delim is not "." assume is a whole class
					// check if class exists in cleanroom
					getCleanroomClass(arg);
				}
			}
		}
		// check annotations on method level
		long timeoutSum = 0;
		HashSet<String> usedExercises = new HashSet<>();
		HashSet<String> bonusExercises = new HashSet<>();
		for (Method testMethod : JUnitWithPoints.getTestMethodsSorted(testClass)) {
			String testMethodName = testClass.getName() + "." + testMethod.getName();
			Test testAnnotation = testMethod.getAnnotation(Test.class);
			Points pointsAnnotation = testMethod.getAnnotation(Points.class);
			System.out.println(testMethodName);
			if (testAnnotation != null && pointsAnnotation == null) {
				throw new AnnotationFormatError("ERROR - found test case with @Test but no @Points annotation: [" + testMethodName + "]");
			} else if (pointsAnnotation == null) {
				continue;
			}
			Timeout timeout = testMethod.getAnnotation(Timeout.class);
			if (timeout == null) {
				timeout = Points.class.getAnnotation(Timeout.class);
			}
			if (timeout == null) {
				throw new AnnotationFormatError("ERROR - found test case without 'timeout' in @Test or @Points annotation: [" + testMethodName + "]");
			}
			timeoutSum += TimeUnit.MILLISECONDS.convert(timeout.value(), timeout.unit());
			Replace replaceAnnotation = testMethod.getAnnotation(Replace.class);
			if (!isSecretClass && replaceAnnotation != null) {
				throw new AnnotationFormatError("ERROR - found test case with @Replace in a public test class: [" + testMethodName + "]");
			} else if (pointsAnnotation.exID().trim().isEmpty()) {
				throw new AnnotationFormatError("ERROR - found test case with empty exercise id in @Points annotation: [" + testMethodName + "]");
			} else if (!exerciseHashMap.containsKey(pointsAnnotation.exID())) {
				throw new AnnotationFormatError("ERROR - found test case with non-declared exercise id in @Points annotation: [" + testMethodName + "]");
			} else if (pointsAnnotation.malus() == 0 || pointsAnnotation.bonus() == 0) {
				throw new AnnotationFormatError("ERROR - found test case with illegal bonus/malus value in @Points annotation: [" + testMethodName + "]");
			} else if (pointsAnnotation.malus() == -1 && pointsAnnotation.bonus() == -1) {
				throw new AnnotationFormatError("ERROR - found test case without bonus/malus value in @Points annotation: [" + testMethodName + "]");
			} else if (pointsAnnotation.bonus() != -1) {
				bonusExercises.add(pointsAnnotation.exID());
			}
			usedExercises.add(pointsAnnotation.exID());
		}
		if (!isSecretClass && usedExercises.size() != exerciseHashMap.size()) {
			throw new AnnotationFormatError("ERROR - found @Ex declaration without corresponding test method: [" + testClass.getName() + "]");
		}
		if (!isSecretClass && bonusExercises.size() != exerciseHashMap.size()) {
			throw new AnnotationFormatError("ERROR - found @Ex declaration without test method with bonus values: [" + testClass.getName() + "]");
		}
		if (timeoutSum > MAX_TIMEOUT_MS) {
			throw new AnnotationFormatError("ERROR - total timeout sum is too high, please reduce to max. " + MAX_TIMEOUT_MS + "ms: [" + timeoutSum + "ms]");
		}
		// check @InitializeOnce annotations
		for (final Field f : testClass.getDeclaredFields()) {
			final InitializeOnce initOnce = f.getAnnotation(InitializeOnce.class);
			if (initOnce != null) {
				if ((f.getModifiers() & Modifier.STATIC) == 0) {
					throw new AnnotationFormatError("ERROR - @InitializeOnce requires a static field");
				}
				if (!f.getType().isPrimitive() && !java.io.Serializable.class.isAssignableFrom(f.getType())) {
					throw new AnnotationFormatError("ERROR - @InitializeOnce requires Serializable type");
				}
				// search given method
				try {
					final Method method = testClass.getDeclaredMethod(initOnce.value());
					if ((method.getModifiers() & Modifier.STATIC) == 0) {
						throw new AnnotationFormatError("ERROR - @InitializeOnce requires a static method");
					}
					if (!f.getType().isAssignableFrom(method.getReturnType())) {
						throw new AnnotationFormatError("ERROR - cannot assign result of @InitializeOnce");
					}
				} catch (final NoSuchMethodException e) {
					throw new AnnotationFormatError("ERROR - invalid @InitializeOnce method \"" + initOnce.value() + "\"");
				}
			}
		}
	}

	// checks if given class exists in cleanroom
	private static Class<?> getCleanroomClass(String name) {
		try (URLClassLoader cleanroomLoader = new URLClassLoader(new URL[]{new File(cwd, "cleanroom").toURI().toURL()})) {
			try {
				return cleanroomLoader.loadClass(name);
			} catch (ClassNotFoundException classNotFoundException) {
				throw new IllegalArgumentException("ERROR - Class [" + classNotFoundException.getMessage() + "] specified in @CompareInterface does not exist in cleanroom");
			}
		} catch (IOException exception) {
			throw new Error("Error - " + exception.getMessage());
		}
	}

	private static Method getMethod(Class<?> cleanroomClass, String methodName) {
		for (Method cleanroomMethod : cleanroomClass.getDeclaredMethods()) {
			if (cleanroomMethod.getName().equals(methodName)) {
				return cleanroomMethod;
			}
		}
		return null;
	}
}
