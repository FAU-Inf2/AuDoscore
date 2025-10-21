package tools;

import tester.annotations.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.*;
import java.lang.reflect.*;
import java.lang.annotation.*;
import org.junit.runner.*;

public class ReplaceManager {
	private static final String cwd = System.getProperty("user.dir");

	// usage:
	// 1) ReplaceManager <secret test class>
	// 2) ReplaceManager --loop -p <public test class> <secret test class>
	static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.err.println("missing argument: secret test class name or option --loop");
			System.exit(-1);
		}
		if (!args[0].equals("--loop")) {
			generateSecretTestReplacements(args[0]);
		} else {
			generateSecretTestRunLoopScript(args[1], args[2]);
		}
	}

	public static String getCanonicalReplacement(Description description) {
		if (description.getAnnotation(Replace.class) != null) {
			Replace r = description.getAnnotation(Replace.class);
			return getCanonicalReplacement(r);
		}
		return "";
	}

	private static void generateSecretTestReplacements(String secretTestClassName) throws ClassNotFoundException {
		Path cleanroom = new File(cwd, "cleanroom").toPath();
		Path student = new File(cwd, "student").toPath();
		try (URLClassLoader unitLoader = new URLClassLoader(new URL[]{new File(cwd, "junit").toURI().toURL()})) {
			Class<?> secretTestClass = unitLoader.loadClass(secretTestClassName);
			for (Method testCaseMethod : getMethodsSorted(secretTestClass)) {
				if (testCaseMethod.isAnnotationPresent(Replace.class)) {
					Replace r = testCaseMethod.getAnnotation(Replace.class);
					if (canProcess(r)) {
						Map<String, SortedSet<String>> methodsMap = getMap(r);
						for (Map.Entry<String, SortedSet<String>> entry : methodsMap.entrySet()) {
							String classToReplace = entry.getKey();
							SortedSet<String> methodsToReplace = entry.getValue();
							final StringBuilder replacedFolderNameSB = new StringBuilder(classToReplace);
							if (methodsToReplace.isEmpty()) {
								continue;
							}
							for (String methodToReplace : methodsToReplace) {
								replacedFolderNameSB.append('#').append(methodToReplace);
							}
							File mixedPath = new File(cwd, replacedFolderNameSB.toString());
							if (!mixedPath.exists() && !mixedPath.mkdir()) {
								throw new AnnotationFormatError("ERROR - Cannot create mixed folder: " + replacedFolderNameSB);
							}
							ReplaceMixer.replace(classToReplace, methodsToReplace, cleanroom, student, mixedPath.toPath());
						}
					}
				}
			}
		} catch (IOException malformedURLException) {
			throw new Error("Error " + malformedURLException.getMessage());
		}
	}

	private static Method[] getMethodsSorted(final Class<?> clazz) {
		final Method[] methods = clazz.getMethods();
		Arrays.sort(methods, Comparator.comparing(Method::getName));
		return methods;
	}

	private static String getCanonicalReplacement(Replace r) {
		Map<String, SortedSet<String>> mMethsMap = getMap(r);
		final StringBuilder canonicalRepresentation = new StringBuilder();
		for (Map.Entry<String, SortedSet<String>> entry : mMethsMap.entrySet()) {
			canonicalRepresentation.append('@').append(entry.getKey()); // class
			for (String method : entry.getValue()) {
				canonicalRepresentation.append('#').append(method);
			}
		}
		return canonicalRepresentation.toString();
	}

	private static Map<String, SortedSet<String>> getMap(Replace r) {
		Map<String, SortedSet<String>> mMethsMap = new TreeMap<>();
		for (final String valueString : r.value()) {
			final int s = valueString.indexOf('.');
			String className;
			String regex;
			if (s == -1) {
				className = valueString;
				regex = ".*";
			} else {
				className = valueString.substring(0, s);
				regex = valueString.substring(s + 1);
			}
			if (!mMethsMap.containsKey(className)) {
				mMethsMap.put(className, new TreeSet<>());
			}
			final SortedSet<String> methods = mMethsMap.get(className);
			try (URLClassLoader cleanroomLoader = new URLClassLoader(new URL[]{new File(cwd, "interfaces").toURI().toURL(), new File(cwd, "cleanroom").toURI().toURL()})) {
				boolean foundMatch = false;
				for (Method method : cleanroomLoader.loadClass(className).getDeclaredMethods()) {
					if (method.getName().matches(regex)) {
						methods.add(method.getName());
						foundMatch = true;
					}
				}
				if ("<init>".matches(regex)) {
					methods.add("<init>");
					foundMatch = true;
				}
				if (!foundMatch) {
					throw new AnnotationFormatError("ERROR - Cannot replace unknown method: " + regex);
				}
			} catch (ClassNotFoundException | IOException e) {
				throw new AnnotationFormatError("ERROR - Cannot replace unknown class: " + className);
			}
		}
		return mMethsMap;
	}

	private static Class<?> getClassFromDescriptor(final String[] descParts) {
		final String[] classParts = new String[descParts.length - 1];
		System.arraycopy(descParts, 0, classParts, 0, classParts.length);
		try (URLClassLoader studentLoader = new URLClassLoader(new URL[]{new File(cwd, "interfaces").toURI().toURL(), new File(cwd, "student").toURI().toURL()})) {
			return studentLoader.loadClass(String.join(".", classParts));
		} catch (final ClassNotFoundException | IOException e) {
			throw new AnnotationFormatError("ERROR - Class '" + String.join(".", classParts) + "' does not exist");
		}
	}

	private static Field getFieldFromDescriptor(final String[] descParts) {
		final Class<?> cls = getClassFromDescriptor(descParts);
		try {
			return cls.getDeclaredField(descParts[descParts.length - 1]);
		} catch (final NoSuchFieldException e) {
			return null;
		}
	}


	private static List<Method> getMethodsFromDescriptor(final String[] descParts) {
		final Class<?> cls = getClassFromDescriptor(descParts);
		final List<Method> result = new ArrayList<>();
		for (final Method method : cls.getDeclaredMethods()) {
			if (method.getName().equals(descParts[descParts.length - 1])) {
				result.add(method);
			}
		}
		return result;
	}

	private static Class<?> getTypeFromDescriptor(final String desc) {
		if (desc.endsWith("[]")) {
			final Class<?> componentType = getTypeFromDescriptor(desc.substring(0, desc.length() - 2));
			return Array.newInstance(componentType, 0).getClass();
		}
		switch (desc) {
			case "byte" -> {
				return byte.class;
			}
			case "boolean" -> {
				return boolean.class;
			}
			case "char" -> {
				return char.class;
			}
			case "double" -> {
				return double.class;
			}
			case "float" -> {
				return float.class;
			}
			case "int" -> {
				return int.class;
			}
			case "long" -> {
				return long.class;
			}
			case "short" -> {
				return short.class;
			}
			case "void" -> {
				return void.class;
			}
			default -> {
				try (URLClassLoader studentLoader = new URLClassLoader(new URL[]{new File(cwd, "interfaces").toURI().toURL(), new File(cwd, "student").toURI().toURL()})) {
					return studentLoader.loadClass(desc);
				} catch (final ClassNotFoundException | IOException e) {
					throw new AnnotationFormatError("ERROR - Invalid type descriptor: " + desc);
				}
			}
		}
	}

	private static boolean canProcess(final Replace replace) {
		// replace.onlyIf() is never 'null' because 'null' is not an allowed ElementValue
		if (replace.onlyIf().isEmpty()) {
			return true;
		}
		final String[] parts = replace.onlyIf().split(";");
		if (parts.length < 3) {
			throw new AnnotationFormatError("ERROR - Invalid 'onlyIf' value: " + replace.onlyIf());
		}
		final String[] descParts = parts[1].split("\\.");
		if (descParts.length < 2) {
			throw new AnnotationFormatError("ERROR - Invalid 'onlyIf' descriptor: " + parts[1]);
		}
		switch (parts[0]) {
			case "field" -> {
				final Field field = getFieldFromDescriptor(descParts);
				if (field == null) {
					return false;
				}
				return field.getType().equals(getTypeFromDescriptor(parts[2]));
			}
			case "method" -> {
				final List<Method> methods = getMethodsFromDescriptor(descParts);
				outer:
				for (final Method method : methods) {
					if (parts.length - 3 == method.getParameterCount() && method.getReturnType().equals(getTypeFromDescriptor(parts[2]))) {
						int i = 0;
						for (final Class<?> paramType : method.getParameterTypes()) {
							if (!paramType.equals(getTypeFromDescriptor(parts[3 + i++]))) {
								continue outer;
							}
						}
						return true;
					}
				}
				return false;
			}
			default -> throw new AnnotationFormatError("ERROR - Invalid 'onlyIf' type: " + parts[0]);
		}
	}

	private static void generateSecretTestRunLoopScript(String publicTestClassName, String secretTestClassName) throws ClassNotFoundException {
		// maps a replacement string ("canonicalReplacement") to all secret test case methods ("suitableTestCaseMethods") with the same replacement declaration
		LinkedHashMap<String, List<String>> replacementMap = new LinkedHashMap<>();
		try (URLClassLoader unitLoader = new URLClassLoader(new URL[]{new File(cwd, "junit").toURI().toURL()})) {
			Class<?> secretTestClass = unitLoader.loadClass(secretTestClassName);
			for (Method testCaseMethod : getMethodsSorted(secretTestClass)) {
				if (testCaseMethod.isAnnotationPresent(Replace.class)) {
					Replace r = testCaseMethod.getAnnotation(Replace.class);
					if (canProcess(r)) {
						String canonicalReplacement = getCanonicalReplacement(r);
						List<String> suitableTestCaseMethods = replacementMap.computeIfAbsent(canonicalReplacement, _ -> new ArrayList<>());
						suitableTestCaseMethods.add(testCaseMethod.getName());
						replacementMap.put(canonicalReplacement, suitableTestCaseMethods);
					}
				}
			}
		} catch (IOException malformedURLException) {
			throw new Error("Error " + malformedURLException.getMessage());
		}
		boolean needSep = false;
		for (Map.Entry<String, List<String>> pair : replacementMap.entrySet()) {
			if (needSep) {
				System.out.println("echo \",\" 1>&2");
			} else {
				needSep = true;
			}
			String canonicalReplacement = pair.getKey();
			List<String> suitableTestCaseMethods = pair.getValue();
			String classpath = canonicalReplacement.substring(1).replaceAll("@", ":").replaceAll("<", "\\\\<").replaceAll(">", "\\\\>");
			boolean first = true;
			for (String suitableTestCaseMethod : suitableTestCaseMethods) {
				if (first) {
					first = false;
				} else {
					System.out.println("echo \",\" 1>&2");
				}
				System.out.println("javac" // recompile SecretTest to cope with boxing, if student and cleanroom have different signatures
						+ " -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/hamcrest-core.jar:lib/junitpoints.jar:" + classpath + ":junit:interfaces:student" //
						+ " -d " + classpath //
						+ " -sourcepath junit" //
						+ " junit/" + secretTestClassName + ".java");
				System.out.println("java" // execute specific test case
						+ " -XX:-OmitStackTraceInFastThrow -Xmx1024m" //
						+ " -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/hamcrest-core.jar:lib/junitpoints.jar:" + classpath + ":junit:interfaces:student" //
						+ " -Dpub=" + publicTestClassName //
						+ " -Djson=yes tools.SingleMethodRunner " + secretTestClassName + " " + suitableTestCaseMethod);
			}
		}
	}
}
