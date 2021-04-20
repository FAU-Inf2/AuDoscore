import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;

import tester.annotations.*;

import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.io.File;

public class CheckAnnotation {

	public static final int MAX_TIMEOUT_MS = 60_000;
	private static String cwd = System.getProperty("user.dir");


	// checks if given class exists in cleanroom
	private static Class<?> getCleanroomClass(String name){
		String pathToCleanroom = cwd + "/cleanroom/";
		ClassLoader cleanroomLoader;

		try{
			cleanroomLoader = new URLClassLoader(new URL[]{new File(pathToCleanroom).toURI().toURL(), new File(cwd).toURI().toURL()});
		} catch (MalformedURLException mfue) {
			throw new Error("Error - " + mfue.getMessage());
		}

		Class<?> cleanroomClass;

		try{
			cleanroomClass = cleanroomLoader.loadClass(name);
		} catch (ClassNotFoundException cnfe) {
			throw new IllegalArgumentException("ERROR - Class ["+cnfe.getMessage() +"] specified in @CompareInterface does not exist in cleanroom");
		}

		return cleanroomClass;
	}

	private static Method getMethod(Class<?> cleanroomClass, String methodName){
		for(Method cleanroomMethod : cleanroomClass.getDeclaredMethods()){
			if(cleanroomMethod.getName().equals(methodName)){
				return cleanroomMethod;
			}
		}
		return null;
	}

	// checks (almost) all annotation conditions
	public static void checkAnnotations(Class<?> clazz, Exercises exercisesAnnotation) {
		// check annotations on class level
		if (exercisesAnnotation == null || exercisesAnnotation.value().length == 0) {
			throw new AnnotationFormatError("ERROR - did not find valid @Exercises declaration: [" + clazz.getName() + "]");
		}

		final HashMap<String, Ex> exerciseHashMap = new HashMap<>();
		for (Ex exercise : exercisesAnnotation.value()) {
			if (exercise.exID().trim().length() == 0) {
				throw new AnnotationFormatError("ERROR - found @Exercises annotation with empty exercise name and following points: [" + exercise.points() + "]");
			} else if (exercise.points() <= 0) {
				throw new AnnotationFormatError("ERROR - found @Exercises annotation with illegal points value: [" + exercise.exID() + "]");
			} else if (exerciseHashMap.containsKey(exercise.exID())) {
				throw new AnnotationFormatError("ERROR - found @Exercises annotation with duplicate exercise: [" + exercise.exID() + "]");
			} else {
				exerciseHashMap.put(exercise.exID(), exercise);
			}
		}

		SecretClass secretClassAnnotation = clazz.getAnnotation(SecretClass.class);
		boolean isSecretClass = secretClassAnnotation != null;

		// check if there are methods to compare with cleanroom counteparts
		CompareInterface compareInterfaceAnnotation = clazz.getAnnotation(CompareInterface.class);
		if(compareInterfaceAnnotation != null){
			for(String arg : compareInterfaceAnnotation.value()){
				if(arg.contains(".")){
					String[] parts = arg.split("\\.");
					if(parts.length != 2){
						throw new IllegalArgumentException("ERROR - @CompareInterface must look like this: Class, Class.Method or Class.Field, found: " + arg);
					}
					// first part is classname, second part method or field
					Class<?> cleanroomClass = getCleanroomClass(parts[0]);
					// TODO avoid reloading same class
					if(getMethod(cleanroomClass,parts[1]) == null){
						// method does not exists check Field

						try{
							cleanroomClass.getField(parts[1]);
						} catch (NoSuchFieldException nsfe){
							throw new IllegalArgumentException("ERROR - " + arg + " specified in @CompareInterface could not be found in cleanroom");
						}
					}

				}else{
					// delim is not "." assume is a whole class
					// check if class exists in cleanroom
					getCleanroomClass(arg);
				}
			}
		}

		final Optional<Long> classTimeout;
		if (clazz.getAnnotation(Timeout.class) != null) {
			final Timeout timeout = clazz.getAnnotation(Timeout.class);
			classTimeout = Optional.of(timeout.unit().toMillis(timeout.value()));
		} else {
			classTimeout = Optional.empty();
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
			final Timeout timeout = m.getAnnotation(Timeout.class);
			if (timeout != null && timeout.value() >= 0) {
				timeoutSum += timeout.unit().toMillis(timeout.value());
			} else if (classTimeout.isPresent() && classTimeout.get() >= 0) {
				timeoutSum += classTimeout.get();
			} else {
				throw new AnnotationFormatError("ERROR - found test case without @Timeout annotation: [" + clazz.getName() + "]");
			}

			Bonus bonusAnnotation = m.getAnnotation(Bonus.class);
			Malus malusAnnotation = m.getAnnotation(Malus.class);
			Points pointsAnnotation = m.getAnnotation(Points.class);
			Replace replaceAnnotation = m.getAnnotation(Replace.class);
			SecretCase secretCaseAnnotation = m.getAnnotation(SecretCase.class);

			if(secretCaseAnnotation != null && !isSecretClass){
				throw new AnnotationFormatError("ERROR - found test case with deprecated @SecretCase annotation in public test [" + clazz.getName() + "]");
			}

			if (bonusAnnotation != null || malusAnnotation != null) {
				throw new AnnotationFormatError("ERROR - found test case with deprecated @Bonus/@Malus annotation [" + clazz.getName() + "]");
			} else if (pointsAnnotation == null) {
				throw new AnnotationFormatError("ERROR - found test case without @Points annotation [" + clazz.getName() + "]");
			} else if (!isSecretClass && replaceAnnotation != null) {
				throw new AnnotationFormatError("ERROR - found test case with @Replace in a public test class: [" + clazz.getName() + "]");
			} else if (pointsAnnotation.exID().trim().length() == 0) {
				throw new AnnotationFormatError("ERROR - found test case with empty exercise id in @Points annotation: [" + clazz.getName() + "]");
			} else if (!exerciseHashMap.containsKey(pointsAnnotation.exID())) {
				throw new AnnotationFormatError("ERROR - found test case with non-declared exercise id in @Points annotation: [" + clazz.getName() + "]");
			} else if (pointsAnnotation.malus() == 0 || pointsAnnotation.bonus() == 0) {
				throw new AnnotationFormatError("ERROR - found test case with illegal bonus/malus value in @Points annotation: [" + clazz.getName() + "]");
			} else if (pointsAnnotation.malus() == -1 && pointsAnnotation.bonus() == -1) {
				throw new AnnotationFormatError("ERROR - found test case without bonus/malus value in @Points annotation: [" + clazz.getName() + "]");
			} else if (pointsAnnotation.bonus() != -1) {
				bonusExercises.add(pointsAnnotation.exID());
			}
			usedExercises.add(pointsAnnotation.exID());
		}
		if (!isSecretClass && usedExercises.size() != exerciseHashMap.size()) {
			throw new AnnotationFormatError("ERROR - found @Ex declaration without corresponding test method: [" + clazz.getName() + "]");
		}
		if (!isSecretClass && bonusExercises.size() != exerciseHashMap.size()) {
			throw new AnnotationFormatError("ERROR - found @Ex declaration without test method with bonus values: [" + clazz.getName() + "]");
		}
		if (timeoutSum > MAX_TIMEOUT_MS) {
			throw new AnnotationFormatError("ERROR - total timeout sum is too high, please reduce to max. " + MAX_TIMEOUT_MS + "ms: [" + timeoutSum + "ms]");
		}

		// check for PointsLogger and PointsSummary
		boolean hasLogger = false;
		boolean hasSummary = false;
		for (Field f : clazz.getFields()) {
			if (JUnitWithPoints.PointsLogger.class.isAssignableFrom(f.getType())) {
				if (hasLogger) {
					throw new AnnotationFormatError("ERROR - found PointsLogger twice; what are you trying to do?");
				}
				RegisterExtension rule = f.getAnnotation(RegisterExtension.class);
				if (rule == null) {
					throw new AnnotationFormatError("ERROR - found PointsLogger but @RegisterExtension annotation is missing");
				}
				hasLogger = true;
			}
			if (JUnitWithPoints.PointsSummary.class.isAssignableFrom(f.getType())) {
				if (hasSummary) {
					throw new AnnotationFormatError("ERROR - found PointsSummary twice; what are you trying to do?");
				}
				RegisterExtension rule = f.getAnnotation(RegisterExtension.class);
				if (rule == null) {
					throw new AnnotationFormatError("ERROR - found PointsSummary but @RegisterExtension annotation is missing");
				}
				hasSummary = true;
			}
		}
		if (!hasLogger) {
			throw new AnnotationFormatError("ERROR - found no valid PointsLogger in test class");
		}
		if (!hasSummary) {
			throw new AnnotationFormatError("ERROR - found no valid PointsSummary in test class");
		}

		// check @InitializeOnce annotations
		for (final Field f : clazz.getDeclaredFields()) {
			final InitializeOnce initOnce = f.getAnnotation(InitializeOnce.class);
			if (initOnce != null) {
				if ((f.getModifiers() & Modifier.STATIC) == 0) {
					throw new AnnotationFormatError("ERROR - @InitializeOnce requires a static field");
				}
				if (!f.getType().isPrimitive()
						&& !java.io.Serializable.class.isAssignableFrom(f.getType())) {
					throw new AnnotationFormatError("ERROR - @InitializeOnce requires Serializable type");
				}

				// Search given method
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

	public static void main(String[] args) throws ClassNotFoundException {
		ClassLoader unitLoader;
		try{
			unitLoader = new URLClassLoader(new URL[]{new File(cwd).toURI().toURL()});
		} catch (MalformedURLException mfue) {
			throw new Error("Error " + mfue.getMessage());
		}

		Class<?> clazz;

		try{
			clazz = unitLoader.loadClass(args[0]);
		} catch (ClassNotFoundException cnfe) {
			throw new IllegalArgumentException("ERROR - Class ["+cnfe.getMessage() +"] specified in @CompareInterface does not exist");
		}

		Exercises exercisesAnnotation = JUnitWithPoints.PointsSummary.getExercisesAnnotation(clazz);
		checkAnnotations(clazz, exercisesAnnotation);
	}
}
