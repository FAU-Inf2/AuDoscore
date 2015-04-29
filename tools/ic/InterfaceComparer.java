package tools.ic;

import java.util.*;
import java.lang.reflect.*;
import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;

public class InterfaceComparer {
	private static boolean error = false;
	private static void compareClasses(Class<?> cleanroomClass, Class<?> studentClass) {
		boolean equals = false;
		ArrayList<Method> studentMethods = new ArrayList<Method>(Arrays.asList(studentClass.getDeclaredMethods()));
		// checkMethods
		for(Method cleanroomMethod : cleanroomClass.getDeclaredMethods()){
			System.out.println("[cleanroom]" + cleanroomMethod);
			for(Method studentMethod : studentMethods) {
				System.out.println("[student]" + studentMethod);
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
				System.err.println("WARNING - Method " +cleanroomMethod + " does not exists in student code or does not match with student counterpart");
			}
		}		
	}

	private static String getSimpleFileName(String path){
		int idx = path.lastIndexOf("/");
		return idx >= 0 ? path.substring(idx + 1) : path;
	}


	public static void main(String args[]){
		String pathToCleanroom = "./cleanroom/";
		File f = new File(pathToCleanroom);
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		String cwd = System.getProperty("user.dir");
		File cleanroomJAR = new File(cwd+"/cleanroom/");
		ClassLoader cleanroomLoader = null;
		ClassLoader studentLoader = null;
		try{
			cleanroomLoader = new URLClassLoader(new URL[]{cleanroomJAR.toURI().toURL()});
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

		if(error){
			throw new Error();
		}
	}
}
