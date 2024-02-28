package tools.ic;

import tester.annotations.CompareInterface;

import java.util.*;
import java.lang.reflect.*;
import java.net.*;
import java.io.*;

public class InterfaceComparator {
	private static boolean error = false;
	private static HashMap<String, HashMap<String, Boolean>> checkMap = null;

	// retrieve a Field by name from given class
	private static Field getField(Class<?> clazz, String name) {
		try {
			return clazz.getField(name);
		} catch (NoSuchFieldException ignored) {
			// ignore
		}
		return null;
	}

	// retrieve all methods from given class (map: method name => Method object)
	private static HashMap<String, Method> getMethodMapForClass(Class<?> clazz) {
		HashMap<String, Method> methodMap = new HashMap<>();
		for (Method method : clazz.getDeclaredMethods()) {
			methodMap.put(method.getName(), method);
		}
		return methodMap;
	}

	private static Method getDeclaredMethod(final Class<?> cls, final String name, final Class<?>... parameterTypes) throws NoSuchMethodException {
		// we cannot use cls.getDeclaredMethod() here, because the parameter types may be loaded by a different ClassLoader.
		// Hence, we compare the string representations of the parameters.
		outer:
		for (final Method m : cls.getDeclaredMethods()) {
			if (m.getName().equals(name) && m.getParameterCount() == parameterTypes.length) {
				for (int i = 0; i < parameterTypes.length; ++i) {
					if (!m.getParameterTypes()[i].toString().equals(parameterTypes[i].toString())) {
						continue outer;
					}
				}
				return m;
			}
		}
		throw new NoSuchMethodException();
	}

	// create a map of all type variables to replacement strings
	private static Map<TypeVariable<? extends GenericDeclaration>, String> getTypeVariableReplacements(final Member member) {
		final Map<TypeVariable<? extends GenericDeclaration>, String> result = new HashMap<>();
		int idx = 0;
		for (final TypeVariable<?> typeVar : member.getDeclaringClass().getTypeParameters()) {
			result.put(typeVar, "$TV" + idx);
			idx += 1;
		}
		if (member instanceof GenericDeclaration) {
			for (final TypeVariable<?> typeVar : ((GenericDeclaration) member).getTypeParameters()) {
				result.put(typeVar, "$TV" + idx);
				idx += 1;
			}
		}
		return result;
	}

	// convert a Type to a normalized string (including type variables)
	private static String toNormalizedString(final Type type, final Map<TypeVariable<? extends GenericDeclaration>, String> replacements) {
		if (replacements.containsKey(type)) {
			return replacements.get(type);
		}
		if (type instanceof GenericArrayType) {
			return toNormalizedString(((GenericArrayType) type).getGenericComponentType(), replacements) + "[]";
		} else if (type instanceof final ParameterizedType paramType) {
			final StringBuilder resultBuilder = new StringBuilder();
			if (paramType.getOwnerType() != null) {
				resultBuilder.append(toNormalizedString(paramType.getOwnerType(), replacements)).append('.');
			}
			resultBuilder.append(paramType.getRawType().getTypeName());
			final Type[] typeArguments = paramType.getActualTypeArguments();
			if (typeArguments.length > 0) {
				resultBuilder.append('<');
				for (int i = 0; i < typeArguments.length; ++i) {
					if (i > 0) {
						resultBuilder.append(',');
					}
					resultBuilder.append(toNormalizedString(typeArguments[i], replacements));
				}
				resultBuilder.append('>');
			}
			return resultBuilder.toString();
		} else if (type instanceof final WildcardType wildcardType) {
			final StringBuilder resultBuilder = new StringBuilder();
			resultBuilder.append('?');
			final Type[] lowerBounds = wildcardType.getLowerBounds();
			if (lowerBounds == null || lowerBounds.length == 0) {
				final Type[] upperBounds = wildcardType.getUpperBounds();
				resultBuilder.append(" extends ").append(toNormalizedString(upperBounds[0], replacements));
			} else {
				resultBuilder.append(" super ").append(toNormalizedString(lowerBounds[0], replacements));
			}
			return resultBuilder.toString();
		}
		return type.getTypeName();
	}

	// convert Field to a normalized String (including type variables)
	private static String toNormalizedString(final Field field) {
		final Map<TypeVariable<? extends GenericDeclaration>, String> replacements = getTypeVariableReplacements(field);
		final StringBuilder resultBuilder = new StringBuilder();
		final int modifiers = field.getModifiers();
		if (modifiers != 0) {
			resultBuilder.append(Modifier.toString(modifiers)).append(' ');
		}
		resultBuilder.append(toNormalizedString(field.getGenericType(), replacements));
		return resultBuilder.append(' ').append(field.getDeclaringClass().getName()).append('.').append(field.getName()).toString();
	}

	// convert Method to a normalized String (including type variables) but without the thrown exceptions
	private static String toNormalizedString(final Method method) {
		final Map<TypeVariable<? extends GenericDeclaration>, String> replacements = getTypeVariableReplacements(method);
		final StringBuilder resultBuilder = new StringBuilder();
		final int modifiers = method.getModifiers() & Modifier.methodModifiers();
		if (modifiers != 0) {
			resultBuilder.append(Modifier.toString(modifiers)).append(' ');
		}
		final TypeVariable<?>[] typeParams = method.getTypeParameters();
		if (typeParams.length > 0) {
			resultBuilder.append('<');
			boolean first = true;
			for (final TypeVariable<?> typeParam : typeParams) {
				if (!first) {
					resultBuilder.append(',');
				}
				resultBuilder.append(replacements.get(typeParam));
				first = false;
			}
			resultBuilder.append('>');
		}
		resultBuilder.append(toNormalizedString(method.getGenericReturnType(), replacements)).append(' ').append(method.getDeclaringClass().getName()).append('.').append(method.getName()).append('(');
		final Type[] params = method.getGenericParameterTypes();
		for (int i = 0; i < params.length; ++i) {
			if (i > 0) {
				resultBuilder.append(',');
			}
			String paramName = toNormalizedString(params[i], replacements);
			if (method.isVarArgs() && i == params.length - 1) {
				paramName = paramName.replaceFirst("\\[\\]$", "...");
			}
			resultBuilder.append(paramName);
		}
		resultBuilder.append(')');
		return resultBuilder.toString();
	}

	// check two fields and print err msg. If error occurs false is returned
	private static boolean checkField(Field cleanroomField, Field studentField, Class<?> cleanroomClass) {
		if (studentField == null) {
			System.err.println("ERROR - Field " + cleanroomField + "[" + cleanroomClass.getName() + "] does not exists in student code");
			return false;
		} else {
			if (!toNormalizedString(cleanroomField).equals(toNormalizedString(studentField))) {
				System.err.println("ERROR - Field " + cleanroomField + "[" + cleanroomClass.getName() + "] does not match with student counterpart");
				return false;
			}
		}

		return true;
	}

	// check that every exception type in left is also included in right
	// unless it doesn't have to be declared, i.e. unchecked exception
	private static boolean checkExceptionTypes(Class<?>[] left, Class<?>[] right) {
		List<Class<?>> rightList = Arrays.asList(right);
		for (Class<?> e : left) {
			if (RuntimeException.class.isAssignableFrom(e) || Error.class.isAssignableFrom(e)) {
				// skip unchecked exceptions
				continue;
			}
			if (!rightList.contains(e)) {
				return false;
			}
		}
		return true;
	}


	// check two methods and print err msg. If error occurs false is returned
	private static boolean checkMethod(Method cleanroomMethod, Class<?> studentClass) {
		try {
			final Method studentMethod = getDeclaredMethod(studentClass, cleanroomMethod.getName(), cleanroomMethod.getParameterTypes());
			String cleanString = toNormalizedString(cleanroomMethod);
			String studentString = toNormalizedString(studentMethod);
			if (!cleanString.equals(studentString)) {
				System.err.println("ERROR - Method does not match with student counterpart: ");
				System.err.println("\t * student: " + studentString);
				System.err.println("\t *   clean: " + cleanString);
				return false;

			}
			Class<?>[] cleanExc = cleanroomMethod.getExceptionTypes();
			Class<?>[] studentExc = studentMethod.getExceptionTypes();
			if (!checkExceptionTypes(cleanExc, studentExc) || !checkExceptionTypes(studentExc, cleanExc)) {
				System.err.println("ERROR - Method " + cleanroomMethod + "[" + studentClass.getName() + "] does not match with respect to exception types");
				return false;
			}
		} catch (NoSuchMethodException noSuchMethodException) {
			System.err.println("ERROR - Method " + cleanroomMethod + "[" + studentClass.getName() + "] does not match or does not exist in student code");
			return false;
		}
		return true;
	}

	// check everything
	private static void checkAll(Class<?> cleanroomClass, Class<?> studentClass) {
		// check all methods
		getMethodMapForClass(studentClass);
		for (Method cleanroomMethod : cleanroomClass.getDeclaredMethods()) {
			if (!Modifier.isPublic(cleanroomMethod.getModifiers())) {
				continue;
			}
			boolean success = checkMethod(cleanroomMethod, studentClass);
			if (!success) {
				error = true;
			}
		}
		// check all fields
		for (Field cleanroomField : cleanroomClass.getFields()) {
			if (!Modifier.isPublic(cleanroomField.getModifiers())) {
				continue;
			}
			boolean success = checkField(cleanroomField, getField(studentClass, cleanroomField.getName()), cleanroomClass);
			if (!success) {
				error = true;
			}
		}
	}

	// check for specified cleanroom methods with student counterpart
	private static void compareClasses(Class<?> cleanroomClass, Class<?> studentClass) {
		HashMap<String, Boolean> fieldMethodMap = checkMap.get(cleanroomClass.getName());
		if (fieldMethodMap.isEmpty()) {
			// only class was given
			checkAll(cleanroomClass, studentClass);
			return;
		}
		HashMap<String, Method> cleanroomMethodMap = getMethodMapForClass(cleanroomClass);
		// Check all given values in @CompareInterface
		for (String toCheck : fieldMethodMap.keySet()) {
			// check if it is a field
			Field cleanroomField = getField(cleanroomClass, toCheck);
			if (cleanroomField != null) {
				// get student counterpart
				Field studentField = getField(studentClass, toCheck);
				boolean success = checkField(cleanroomField, studentField, cleanroomClass);
				if (!success) {
					error = true;
				}
				continue;
			}
			// check if it is a method
			Method cleanroomMethod = cleanroomMethodMap.get(toCheck);
			boolean success = checkMethod(cleanroomMethod, studentClass);
			if (!success) {
				error = true;
			}
		}
	}

	// parses the cmd args and save it to HashMap
	private static void valuesToMap(String[] annotationValue) {
		checkMap = new HashMap<>();
		for (String arg : annotationValue) {
			if (arg.contains(".")) {
				String[] parts = arg.split("\\.");
				HashMap<String, Boolean> fieldMethodMap = checkMap.get(parts[0]);
				if (fieldMethodMap == null) {
					fieldMethodMap = new HashMap<>();
				}
				fieldMethodMap.put(parts[1], true);
				checkMap.put(parts[0], fieldMethodMap);
			} else {
				// only Class is given
				HashMap<String, Boolean> fieldMethodMap = new HashMap<>();
				checkMap.put(arg, fieldMethodMap);
			}
		}
	}

	private static String[] extractValueFromUnitTest(String className, ClassLoader classLoader) {
		Class<?> clazz;
		try {
			clazz = classLoader.loadClass(className);
		} catch (ClassNotFoundException classNotFoundException) {
			throw new Error("Error -  test class [" + classNotFoundException.getMessage() + "] not found");
		}
		CompareInterface compareInterfaceAnnotation = clazz.getAnnotation(CompareInterface.class);
		if (compareInterfaceAnnotation == null) {
			System.exit(0);
		}
		// content was check in compile-stage0 step
		return compareInterfaceAnnotation.value();
	}

	public static void main(String[] args) {
		if (args == null) {
			System.err.println("Usage: java tools.ic.InterfaceComparator JUnitTest");
			System.exit(-1);
		}
		String cwd = System.getProperty("user.dir");
		String pathToCleanroom = cwd + "/cleanroom/";
		try (URLClassLoader cleanroomLoader = new URLClassLoader(new URL[]{new File(pathToCleanroom).toURI().toURL(), new File(cwd).toURI().toURL()}); //
			 URLClassLoader studentLoader = new URLClassLoader(new URL[]{new File(cwd).toURI().toURL()})) {
			// extract content from @CompareInterface Annotation
			String[] annotationValue = extractValueFromUnitTest(args[0], studentLoader);
			valuesToMap(annotationValue);
			for (String className : checkMap.keySet()) {
				Class<?> cleanroomClass;
				try {
					cleanroomClass = cleanroomLoader.loadClass(className);
				} catch (ClassNotFoundException classNotFoundException) {
					throw new Error("Error - cleanroom class [" + classNotFoundException.getMessage() + "] not found");
				}
				Class<?> studentClass;
				try {
					studentClass = studentLoader.loadClass(className);
				} catch (ClassNotFoundException classNotFoundException) {
					throw new Error("Error - student class [" + classNotFoundException.getMessage() + "] not found");
				}
				compareClasses(cleanroomClass, studentClass);
			}
		} catch (IOException malformedURLException) {
			throw new Error("Error - " + malformedURLException.getMessage());
		}
		if (error) {
			throw new Error();
		}
	}
}
