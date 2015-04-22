import org.junit.Test;
import org.junit.runner.Description;
import tester.annotations.*;

import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

public class CheckAnnotation {

    public static final int MAX_TIMEOUT_MS = 60_000;

    // checks (almost) all annotation conditions
    public static void checkAnnotations(Description description, Exercises exercisesAnnotation) {
        HashMap<String, Ex> exerciseHashMap = new HashMap<>();

        // check annotations on class level
        if (exercisesAnnotation == null || exercisesAnnotation.value().length == 0) {
            throw new AnnotationFormatError("WARNING - did not find valid @Exercises declaration: [" + description.getDisplayName() + "]");
        }
        for (Ex exercise : exercisesAnnotation.value()) {
            if (exercise.exID().trim().length() == 0) {
                throw new AnnotationFormatError("WARNING - found @Exercises annotation with empty exercise name and following points: [" + exercise.points() + "]");
            } else if (exercise.points() <= 0) {
                throw new AnnotationFormatError("WARNING - found @Exercises annotation with illegal points value: [" + exercise.exID() + "]");
            } else if (exerciseHashMap.containsKey(exercise.exID())) {
                throw new AnnotationFormatError("WARNING - found @Exercises annotation with duplicate exercise: [" + exercise.exID() + "]");
            } else {
                exerciseHashMap.put(exercise.exID(), exercise);
            }
        }

        // check annotations on method level
        long timeoutSum = 0;
        HashSet<String> usedExercises = new HashSet<>(), bonusExercises = new HashSet<>();
        Class<?> clazz = description.getTestClass();
        SecretClass secretClassAnnotation = clazz.getAnnotation(SecretClass.class);
        boolean isSecretClass = secretClassAnnotation != null;
        for (Method m : clazz.getMethods()) {
            Test test = m.getAnnotation(Test.class);
            if (test == null) {
                continue;
            }
            if (test.timeout() == 0) {
                throw new AnnotationFormatError("WARNING - found test case without 'timeout' in @Test annotation: [" + description.getDisplayName() + "]");
            }
            timeoutSum += test.timeout();

            Bonus bonusAnnotation = m.getAnnotation(Bonus.class);
            Malus malusAnnotation = m.getAnnotation(Malus.class);
            Points pointsAnnotation = m.getAnnotation(Points.class);
            Replace replaceAnnotation = m.getAnnotation(Replace.class);
            SecretCase secretCaseAnnotation = m.getAnnotation(SecretCase.class);

            if(secretCaseAnnotation != null && !isSecretClass){
                throw new AnnotationFormatError("WARNING - found test case with deprecated @SecretCase annotation in public test [" + description.getDisplayName() + "]");
            }

            if (bonusAnnotation != null || malusAnnotation != null) {
                throw new AnnotationFormatError("WARNING - found test case with deprecated @Bonus/@Malus annotation [" + description.getDisplayName() + "]");
            } else if (pointsAnnotation == null) {
                throw new AnnotationFormatError("WARNING - found test case without @Points annotation [" + description.getDisplayName() + "]");
            } else if (!isSecretClass && replaceAnnotation != null) {
                throw new AnnotationFormatError("WARNING - found test case with @Replace in a public test class: [" + description.getDisplayName() + "]");
            } else if (pointsAnnotation.exID().trim().length() == 0) {
                throw new AnnotationFormatError("WARNING - found test case with empty exercise id in @Points annotation: [" + description.getDisplayName() + "]");
            } else if (!exerciseHashMap.containsKey(pointsAnnotation.exID())) {
                throw new AnnotationFormatError("WARNING - found test case with non-declared exercise id in @Points annotation: [" + description.getDisplayName() + "]");
            } else if (pointsAnnotation.malus() == 0 || pointsAnnotation.bonus() == 0) {
                throw new AnnotationFormatError("WARNING - found test case with illegal bonus/malus value in @Points annotation: [" + description.getDisplayName() + "]");
            } else if (pointsAnnotation.malus() == -1 && pointsAnnotation.bonus() == -1) {
                throw new AnnotationFormatError("WARNING - found test case without bonus/malus value in @Points annotation: [" + description.getDisplayName() + "]");
            } else if (pointsAnnotation.bonus() != -1) {
                bonusExercises.add(pointsAnnotation.exID());
            }
            usedExercises.add(pointsAnnotation.exID());
        }
        if (usedExercises.size() != exerciseHashMap.size()) {
            throw new AnnotationFormatError("WARNING - found @Ex declaration without corresponding test method: [" + description.getDisplayName() + "]");
        }
        if (bonusExercises.size() != exerciseHashMap.size()) {
            throw new AnnotationFormatError("WARNING - found @Ex declaration without test method with bonus values: [" + description.getDisplayName() + "]");
        }
        if (timeoutSum > MAX_TIMEOUT_MS) {
            throw new AnnotationFormatError("WARNING - total timeout sum is too high, please reduce to max. " + MAX_TIMEOUT_MS + "ms: [" + timeoutSum + "ms]");
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(args[0]);
        Description description = Description.createSuiteDescription(clazz);
        Exercises exercisesAnnotation = JUnitWithPoints.PointsSummary.getExercisesAnnotation(description);
        checkAnnotations(description, exercisesAnnotation);
    }
}
