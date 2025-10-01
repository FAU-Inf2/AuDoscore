import org.junit.*;
import org.junit.runner.*;
import tester.annotations.*;
import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.net.*;

public class CheckAnnotation {
	public static final int MAX_TIMEOUT_MS = 60_000;
	private static final String cwd = System.getProperty("user.dir");

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

	// checks (almost) all annotation conditions
	public static void checkAnnotations(Description description, Exercises exercisesAnnotation) {
		// check annotations on class level
		if (exercisesAnnotation == null || exercisesAnnotation.value().length == 0) {
			throw new AnnotationFormatError("ERROR - did not find valid @Exercises declaration: [" + description.getDisplayName() + "]");
		}
		final HashMap<String, Ex> exerciseHashMap = new HashMap<>();
		for (Ex exercise : exercisesAnnotation.value()) {
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
		Class<?> clazz = description.getTestClass();
		SecretClass secretClassAnnotation = clazz.getAnnotation(SecretClass.class);
		boolean isSecretClass = secretClassAnnotation != null;
		// check if there are methods to compare with cleanroom counterparts
		CompareInterface compareInterfaceAnnotation = clazz.getAnnotation(CompareInterface.class);
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
		for (Method m : clazz.getMethods()) {
			Test test = m.getAnnotation(Test.class);
			if (test == null) {
				continue;
			}
			if (test.timeout() <= 0) {
				throw new AnnotationFormatError("ERROR - found test case without 'timeout' in @Test annotation: [" + description.getDisplayName() + "]");
			}
			timeoutSum += test.timeout();
			Points pointsAnnotation = m.getAnnotation(Points.class);
			Replace replaceAnnotation = m.getAnnotation(Replace.class);
			if (pointsAnnotation == null) {
				throw new AnnotationFormatError("ERROR - found test case without @Points annotation [" + description.getDisplayName() + "]");
			} else if (!isSecretClass && replaceAnnotation != null) {
				throw new AnnotationFormatError("ERROR - found test case with @Replace in a public test class: [" + description.getDisplayName() + "]");
			} else if (pointsAnnotation.exID().trim().isEmpty()) {
				throw new AnnotationFormatError("ERROR - found test case with empty exercise id in @Points annotation: [" + description.getDisplayName() + "]");
			} else if (!exerciseHashMap.containsKey(pointsAnnotation.exID())) {
				throw new AnnotationFormatError("ERROR - found test case with non-declared exercise id in @Points annotation: [" + description.getDisplayName() + "]");
			} else if (pointsAnnotation.malus() == 0 || pointsAnnotation.bonus() == 0) {
				throw new AnnotationFormatError("ERROR - found test case with illegal bonus/malus value in @Points annotation: [" + description.getDisplayName() + "]");
			} else if (pointsAnnotation.malus() == -1 && pointsAnnotation.bonus() == -1) {
				throw new AnnotationFormatError("ERROR - found test case without bonus/malus value in @Points annotation: [" + description.getDisplayName() + "]");
			} else if (pointsAnnotation.bonus() != -1) {
				bonusExercises.add(pointsAnnotation.exID());
			}
			usedExercises.add(pointsAnnotation.exID());
		}
		if (!isSecretClass && usedExercises.size() != exerciseHashMap.size()) {
			throw new AnnotationFormatError("ERROR - found @Ex declaration without corresponding test method: [" + description.getDisplayName() + "]");
		}
		if (!isSecretClass && bonusExercises.size() != exerciseHashMap.size()) {
			throw new AnnotationFormatError("ERROR - found @Ex declaration without test method with bonus values: [" + description.getDisplayName() + "]");
		}
		if (timeoutSum > MAX_TIMEOUT_MS) {
			throw new AnnotationFormatError("ERROR - total timeout sum is too high, please reduce to max. " + MAX_TIMEOUT_MS + "ms: [" + timeoutSum + "ms]");
		}
		// check for @Rule and @ClassRule
		boolean hasRule = false;
		boolean hasClassRule = false;
		for (Field f : clazz.getFields()) {
			if (JUnitWithPoints.PointsLogger.class.isAssignableFrom(f.getType())) {
				if (hasRule) {
					throw new AnnotationFormatError("ERROR - found PointsLogger twice; what are you trying to do?");
				}
				Rule rule = f.getAnnotation(Rule.class);
				if (rule == null) {
					throw new AnnotationFormatError("ERROR - found PointsLogger but @Rule annotation is missing");
				}
				hasRule = true;
			}
			if (JUnitWithPoints.PointsSummary.class.isAssignableFrom(f.getType())) {
				if (hasClassRule) {
					throw new AnnotationFormatError("ERROR - found PointsSummary twice; what are you trying to do?");
				}
				ClassRule rule = f.getAnnotation(ClassRule.class);
				if (rule == null) {
					throw new AnnotationFormatError("ERROR - found PointsSummary but @ClassRule annotation is missing");
				}
				hasClassRule = true;
			}
		}
		if (!hasRule) {
			throw new AnnotationFormatError("ERROR - found no valid @Rule annotation in test class");
		}
		if (!hasClassRule) {
			throw new AnnotationFormatError("ERROR - found no valid @ClassRule annotation in test class");
		}
		// check @InitializeOnce annotations
		for (final Field f : clazz.getDeclaredFields()) {
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
					final Method method = clazz.getDeclaredMethod(initOnce.value());
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

	static void main(String[] args) {
		try (URLClassLoader unitLoader = new URLClassLoader(new URL[]{new File(cwd, "junit").toURI().toURL()})) {
			Class<?> clazz;
			try {
				clazz = unitLoader.loadClass(args[0]);
			} catch (ClassNotFoundException classNotFoundException) {
				throw new IllegalArgumentException("ERROR - Class [" + classNotFoundException.getMessage() + "] (test case) does not exist");
			}
			Description description = Description.createSuiteDescription(clazz);
			Exercises exercisesAnnotation = JUnitWithPoints.PointsSummary.getExercisesAnnotation(description);
			checkAnnotations(description, exercisesAnnotation);
		} catch (IOException malformedURLException) {
			throw new Error("Error " + malformedURLException.getMessage());
		}
	}
}
