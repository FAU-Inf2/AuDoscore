import org.junit.Test;
import org.junit.Rule;
import org.junit.ClassRule;
import org.junit.runner.Description;

import tester.annotations.*;

import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.io.IOException;
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
	ClassLoader cleanroomLoader = null;

	try{
		cleanroomLoader = new URLClassLoader(new URL[]{new File(pathToCleanroom).toURI().toURL(), new File(cwd).toURI().toURL()});
	} catch (MalformedURLException mfue) {
		throw new Error("Error - " + mfue.getMessage());
	}

	Class<?> cleanroomClass = null;

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
    public static void checkAnnotations(Description description, Exercises exercisesAnnotation) {
        HashMap<String, Ex> exerciseHashMap = new HashMap<>();

        // check annotations on class level
        if (exercisesAnnotation == null || exercisesAnnotation.value().length == 0) {
            throw new AnnotationFormatError("ERROR - did not find valid @Exercises declaration: [" + description.getDisplayName() + "]");
        }
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

        Class<?> clazz = description.getTestClass();
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
						Field field = cleanroomClass.getField(parts[1]);
					} catch (NoSuchFieldException nsfe){
						throw new IllegalArgumentException("ERROR - " + arg + " specified in @CompareInterface could not be found in cleanroom" );
					}
				}

			}else{
				// delim is not "." assume is a whole class
				// check if class exists in cleanroom
				Class<?> cleanroomClass = getCleanroomClass(arg);
			}
		}
	}

        // check annotations on method level
        long timeoutSum = 0;
        HashSet<String> usedExercises = new HashSet<>(), bonusExercises = new HashSet<>();
        for (Method m : clazz.getMethods()) {
            Test test = m.getAnnotation(Test.class);
            if (test == null) {
                continue;
            }
            if (test.timeout() == 0) {
                throw new AnnotationFormatError("ERROR - found test case without 'timeout' in @Test annotation: [" + description.getDisplayName() + "]");
            }
            timeoutSum += test.timeout();

            Bonus bonusAnnotation = m.getAnnotation(Bonus.class);
            Malus malusAnnotation = m.getAnnotation(Malus.class);
            Points pointsAnnotation = m.getAnnotation(Points.class);
            Replace replaceAnnotation = m.getAnnotation(Replace.class);
            SecretCase secretCaseAnnotation = m.getAnnotation(SecretCase.class);

            if(secretCaseAnnotation != null && !isSecretClass){
                throw new AnnotationFormatError("ERROR - found test case with deprecated @SecretCase annotation in public test [" + description.getDisplayName() + "]");
            }

            if (bonusAnnotation != null || malusAnnotation != null) {
                throw new AnnotationFormatError("ERROR - found test case with deprecated @Bonus/@Malus annotation [" + description.getDisplayName() + "]");
            } else if (pointsAnnotation == null) {
                throw new AnnotationFormatError("ERROR - found test case without @Points annotation [" + description.getDisplayName() + "]");
            } else if (!isSecretClass && replaceAnnotation != null) {
                throw new AnnotationFormatError("ERROR - found test case with @Replace in a public test class: [" + description.getDisplayName() + "]");
            } else if (pointsAnnotation.exID().trim().length() == 0) {
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
        if (usedExercises.size() != exerciseHashMap.size()) {
            throw new AnnotationFormatError("ERROR - found @Ex declaration without corresponding test method: [" + description.getDisplayName() + "]");
        }
        if (bonusExercises.size() != exerciseHashMap.size()) {
            throw new AnnotationFormatError("ERROR - found @Ex declaration without test method with bonus values: [" + description.getDisplayName() + "]");
        }
        if (timeoutSum > MAX_TIMEOUT_MS) {
            throw new AnnotationFormatError("ERROR - total timeout sum is too high, please reduce to max. " + MAX_TIMEOUT_MS + "ms: [" + timeoutSum + "ms]");
        }

	// check for @Rule and @ClassRule
	boolean hasRule = false, hasClassRule = false;
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
    }

    public static void main(String[] args) throws ClassNotFoundException {
        ClassLoader unitLoader = null;
	try{
		unitLoader = new URLClassLoader(new URL[]{new File(cwd).toURI().toURL()});
	} catch (MalformedURLException mfue) {
		throw new Error("Error " + mfue.getMessage());
	}

        Class<?> clazz = null;
	
	try{
		clazz = unitLoader.loadClass(args[0]);
	} catch (ClassNotFoundException cnfe) {
		throw new IllegalArgumentException("ERROR - Class ["+cnfe.getMessage() +"] specified in @CompareInterface does not exist");
	}

	Description description = Description.createSuiteDescription(clazz);
        Exercises exercisesAnnotation = JUnitWithPoints.PointsSummary.getExercisesAnnotation(description);
        checkAnnotations(description, exercisesAnnotation);
    }
}
