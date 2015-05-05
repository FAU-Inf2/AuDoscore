package tools.ic;

import java.util.*;
import java.lang.reflect.*;
import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;

public class InterfaceComparer {
	private static boolean error = false;
	private static HashMap<String,Boolean> methodMap = null;
	private static void compareClasses(Class<?> cleanroomClass, Class<?> studentClass) {
		boolean equals = false;
		ArrayList<Method> studentMethods = new ArrayList<Method>(Arrays.asList(studentClass.getDeclaredMethods()));
		// checkMethods
		for(Method cleanroomMethod : cleanroomClass.getDeclaredMethods()){
			// only compare if method was specified in @CompareInterface annotation
			if(methodMap.get(cleanroomMethod.getName()) == null){
				continue;
			}

			methodMap.remove(cleanroomMethod.getName());
			
			for(Method studentMethod : studentMethods) {
				if(cleanroomMethod.toString().equals(studentMethod.toString())) {
					equals = true;
					studentMethods.remove(studentMethod);
					break;
				}
			}	

			if(equals){
				equals = false;
			}else{
				error = true;
				System.err.println("ERROR - Method " +cleanroomMethod + " does not exists in student code or does not match with student counterpart");
			}
		}		
	}

	private static String getSimpleFileName(String path){
		int idx = path.lastIndexOf("/");
		return idx >= 0 ? path.substring(idx + 1) : path;
	}

	private static HashMap<String,Boolean> argsToMap(String[] methods){
		HashMap<String,Boolean> methodMap = new HashMap<String,Boolean>();
		for(String method : methods){
			methodMap.put(method,true);		
		}

		return methodMap;
	}

	public static void main(String args[]){
		if(args == null){
			System.err.println("Usage: java tools.ic.InterfaceComparer <method1> .. <methodn>");
			System.exit(-1);
		}
		String cwd = System.getProperty("user.dir");
		String pathToCleanroom = cwd + "/cleanroom/";
		File f = new File(pathToCleanroom);
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		ClassLoader cleanroomLoader = null;
		ClassLoader studentLoader = null;
		methodMap = argsToMap(args);
		try{
			cleanroomLoader = new URLClassLoader(new URL[]{new File(pathToCleanroom).toURI().toURL()});
			studentLoader = new URLClassLoader(new URL[]{new File(cwd).toURI().toURL()});
		}catch(MalformedURLException mfue){
			throw new Error("Error"  + mfue.getMessage());
		}
		for(File path : f.listFiles()) {
			if (path.isFile()) {
				String pathString = path.toString();
				if(pathString.endsWith(".class")){
					// get simple Name
					String fileName = getSimpleFileName(pathString);
					// strip fileextension
					fileName = fileName.substring(0, fileName.lastIndexOf('.'));	
					Class<?> cleanroomClass = null;
					Class<?> studentClass = null;

					try{
						cleanroomClass = cleanroomLoader.loadClass(fileName);
						studentClass = studentLoader.loadClass(fileName);

					} catch (ClassNotFoundException cnfe) {
						throw new Error("Error - class [" + cnfe.getMessage()+"] not found");
					}
					
					compareClasses(cleanroomClass,studentClass);
				}
			}
		}

		// check if there were methods declared in @CompareInterface which could not be found
		if(methodMap.size() != 0) {
			System.err.println("The following methods declared in @CompareInterface could not be found in cleanroom:");
			for(String methodName : methodMap.keySet()){		
				System.err.print(methodName+" ");
			}
			System.err.println("");
			error = true;
		}
		if(error){
			throw new Error();
		}
	}
}
