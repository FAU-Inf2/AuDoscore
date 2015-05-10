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

	// check for specified cleanroom methods with student counterpart
	private static void compareClasses(Class<?> cleanroomClass, Class<?> studentClass) {
		HashMap<String,Boolean> methodMap = checkMap.get(cleanroomClass.getName());
		// checkMethods
		for(Method cleanroomMethod : cleanroomClass.getDeclaredMethods()){
			// only compare if method was specified in @CompareInterface annotation
			if(methodMap.get(cleanroomMethod.getName()) == null){
				continue;
			}

			methodMap.remove(cleanroomMethod.getName());

			Class<?>[] pTypes = cleanroomMethod.getParameterTypes();
			Method studentMethod = null; 
			
			try{
				studentMethod = studentClass.getMethod(cleanroomMethod.getName(),pTypes);
			} catch (NoSuchMethodException nsme){
				System.err.println("ERROR - Method " +cleanroomMethod + "["+cleanroomClass.getName()+"] does not exists in student code or does not match with student counterpart");
				error = true;
				continue;
			}
			
			if(!cleanroomMethod.toGenericString().equals(studentMethod.toGenericString())) {
				error = true;
				System.err.println("ERROR - Method " +cleanroomMethod + "["+cleanroomClass.getName()+"] does not exists in student code or does not match with student counterpart");
			}
		}		
		
		// check if there were methods declared in @CompareInterface which could not be found
		if(methodMap.size() != 0) {
			System.err.println("Error - The following method(s) declared in @CompareInterface could not be found in cleanroom class [" + cleanroomClass.getName()+"]:");
			for(String methodName : methodMap.keySet()){		
				System.err.print(methodName+" ");
			}
			System.err.println("");
			error = true;
		}
	}

	// parses the cmd args and save it to HashMap
	private static void valuesToMap(String[] annotationValue){
		checkMap = new HashMap<String,HashMap<String,Boolean>>();
		for(String arg : annotationValue){
			if(arg.contains(".")){
				String[] parts = arg.split("\\.");
				HashMap<String,Boolean> methodMap = checkMap.get(parts[0]);
				if(methodMap == null){
					methodMap = new HashMap<String,Boolean>();
				}
					methodMap.put(parts[1],true);
					checkMap.put(parts[0],methodMap);
			} else {
			
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
