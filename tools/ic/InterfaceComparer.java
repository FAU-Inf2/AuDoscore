package tools.ic;

import java.util.*;
import java.lang.reflect.*;
import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;

public class InterfaceComparer {
	private static void compareClasses(Class cleanroomClass, Class studentClass) {

		boolean equals = false;
		ArrayList<Method> studentMethods = new ArrayList<Method>(Arrays.asList(studentClass.getDeclaredMethods()));
		// checkMethods
		for(Method cleanroomMethod : cleanroomClass.getDeclaredMethods()){
			for(Method studentMethod : studentMethods) {
				if(cleanroomMethod.equals(studentMethod)) {
					equals = true;
					studentMethods.remove(studentMethod);
					break;
				}

				if(equals){
					equals = false;
				}else{
					System.err.println("WARNING - Method " +cleanroomMethod.getName() + " does not exists in student code or does not match with student counterpart");
				}
			}	
		}		
	}

	private static String getSimpleFileName(String path){
		int idx = path.lastIndexOf("/");
		return idx >= 0 ? path.substring(idx + 1) : path;
	}


	public static void main(String args[]){
		String pathToCleanroom = "./cleanroom";
		URL[] cleanroomSearchUrls = null;
		try {
			cleanroomSearchUrls = new URL[]{ new URL("file://" + pathToCleanroom) };
		
		} catch (MalformedURLException mue) {
			throw new Error("WARNING - " + mue.getMessage());
		}

		File f = new File(pathToCleanroom);
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		ClassLoader ul = new URLClassLoader(cleanroomSearchUrls);

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
						cleanroomClass = ul.loadClass(fileName);
						studentClass = cl.loadClass(fileName);

					} catch (ClassNotFoundException cnfe) {
						throw new Error("WARNING class [" + cnfe.getMessage()+"] not found");
					}
					compareClasses(cleanroomClass,studentClass);
				}
			}
		}
	}
}
