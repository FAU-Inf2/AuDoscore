package tools.ic;
import tester.annotations.CompareInterface;


import java.util.*;
import java.lang.reflect.*;
import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;

public class InterfaceComparer {
	private static boolean error = false;
	private static HashMap<String,HashMap<String,Boolean>> checkMap = null;

	// retrieves a Field from a Class
	private static Field getField(Class<?> clazz, String name){
		try{
			Field field = clazz.getField(name);
			return field;
		} catch(NoSuchFieldException nsfe) {
			// Do nothing
		}
	
		return null;
	}


	// retrieves all Methods and put in a Map
	private static HashMap<String,Method> getMethodMapForClass(Class<?> clazz){
		HashMap<String,Method> methodMap = new HashMap<String,Method>();
		for(Method method : clazz.getDeclaredMethods()){
			methodMap.put(method.getName(),method);
		}
		return methodMap;
	}

	// checks two fields and print err msg. If error occurs false is returned
	private static boolean checkField(Field cleanroomField, Field studentField, Class<?> cleanroomClass){
		if(studentField == null){
			System.err.println("ERROR - Field " +cleanroomField + "["+cleanroomClass.getName() +"]does not exists in student code");
			return false;
		}else{
			if(!cleanroomField.toGenericString().equals(studentField.toGenericString())){
				System.err.println("ERROR - Field " +cleanroomField + "["+cleanroomClass.getName() +"]does not match with student counterpart");
			return false;
			
			}
		}

		return true;
	}


	// checks two methods and print err msg. If error occurs false is returned
	private static boolean checkMethod(Method cleanroomMethod, Class<?> studentClass){
		Method studentMethod = null;
		try {	
			studentMethod = studentClass.getMethod(cleanroomMethod.getName(),cleanroomMethod.getParameterTypes());
			if(!cleanroomMethod.toGenericString().equals(studentMethod.toGenericString())){
				System.err.println("ERROR - Method " +cleanroomMethod + "["+studentClass.getName() +"]does not match with student counterpart");
				return false;

			}
		} catch (NoSuchMethodException nsme){
			System.err.println("ERROR - Method " +cleanroomMethod + "["+studentClass.getName() +"]does not match or does not exists in student code");
			return false;

		}

		return true;
	}
	
	// checks everything
	private static void checkAll(Class<?> cleanroomClass, Class<?> studentClass){
		// check all Methods
		HashMap<String,Method> studentMethodMap = getMethodMapForClass(studentClass);
		for(Method cleanroomMethod : cleanroomClass.getDeclaredMethods()){
			boolean success = checkMethod(cleanroomMethod,studentClass);
			if(!success) {
				error = true;
			}

		}


		for(Field cleanroomField : cleanroomClass.getFields()){
			boolean success = checkField(cleanroomField,getField(studentClass,cleanroomField.getName()),cleanroomClass);
			if(!success) {
				error = true;
			}
		}

	}


	// check for specified cleanroom methods with student counterpart
	private static void compareClasses(Class<?> cleanroomClass, Class<?> studentClass) {
		HashMap<String,Boolean> fieldMethodMap = checkMap.get(cleanroomClass.getName());
		if(fieldMethodMap.size() == 0){
			// only class was given
			checkAll(cleanroomClass,studentClass);
			return;
		}
		HashMap<String,Method> cleanroomMethodMap = getMethodMapForClass(cleanroomClass);

		// Check all given values in @CompareInterface
		for(String toCheck : fieldMethodMap.keySet()){
			// check if its a field
			Field cleanroomField = getField(cleanroomClass,toCheck);
			if(cleanroomField != null){
				// get student counterpart
				Field studentField = getField(studentClass,toCheck);
				boolean success = checkField(cleanroomField,studentField,cleanroomClass);
				if(!success) {
					error = true;
				}
				continue;
			}

			// check if its a method
			Method cleanroomMethod = cleanroomMethodMap.get(toCheck);
			boolean success = checkMethod(cleanroomMethod,studentClass);
			if(!success) {
				error = true;
			}
		}
	}		
		
	// parses the cmd args and save it to HashMap
	private static void valuesToMap(String[] annotationValue){
		checkMap = new HashMap<String,HashMap<String,Boolean>>();
		for(String arg : annotationValue){
			if(arg.contains(".")){
				String[] parts = arg.split("\\.");
				HashMap<String,Boolean> fieldMethodMap = checkMap.get(parts[0]);
				if(fieldMethodMap == null){
					fieldMethodMap = new HashMap<String,Boolean>();
				}
					fieldMethodMap.put(parts[1],true);
					checkMap.put(parts[0],fieldMethodMap);
			} else {
				// Only Class is given
				HashMap<String,Boolean> fieldMethodMap = new HashMap<String,Boolean>();
				checkMap.put(arg,fieldMethodMap);
			}

		}
	}

	private static String[] extractValueFromUnitTest(String className, ClassLoader classLoader){
		Class<?> clazz = null;
		try{
			clazz = classLoader.loadClass(className);	
		} catch (ClassNotFoundException cnfe) {					
			throw new Error("Error -  testclass [" + cnfe.getMessage()+"] not found");
		}
		
		CompareInterface compareInterfaceAnnotation = clazz.getAnnotation(CompareInterface.class);
		if(compareInterfaceAnnotation == null){
			System.exit(0);
		}
		// content was check in compile-stage0 step
		return compareInterfaceAnnotation.value();

	}

	public static void main(String args[]){
		if(args == null){
			System.err.println("Usage: java tools.ic.InterfaceComparer JUnitTest");
			System.exit(-1);
		}

		String cwd = System.getProperty("user.dir");
		String pathToCleanroom = cwd + "/cleanroom/";
		ClassLoader cleanroomLoader = null;
		ClassLoader studentLoader = null;
		try{
			cleanroomLoader = new URLClassLoader(new URL[]{new File(pathToCleanroom).toURI().toURL()});
			studentLoader = new URLClassLoader(new URL[]{new File(cwd).toURI().toURL()});
		}catch(MalformedURLException mfue){
			throw new Error("Error - "  + mfue.getMessage());
		}

		// extract content from @CompareInterface Annotation
		String[] annotationValue = extractValueFromUnitTest(args[0], studentLoader);
		valuesToMap(annotationValue);
		for(String className : checkMap.keySet()){
			Class<?> cleanroomClass = null;
			Class<?> studentClass = null;
			
			try{
				cleanroomClass = cleanroomLoader.loadClass(className);	
			} catch (ClassNotFoundException cnfe) {	
				throw new Error("Error - cleanroom class [" + cnfe.getMessage()+"] not found");
			}
			
			try{
				studentClass = studentLoader.loadClass(className);
			} catch (ClassNotFoundException cnfe) {	
				throw new Error("Error - student class [" + cnfe.getMessage()+"] not found");
			}
			
			compareClasses(cleanroomClass,studentClass);
		}

		if(error){
			throw new Error();
		}
	}
}
