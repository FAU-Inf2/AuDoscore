import java.util.*;
import java.lang.reflect.*;
public class InterfaceComparer {
	private static void usage() {
		System.err.println("java InterfaceComparer <CleanroomClass> <StudentClass>");
		System.exit(1);
	}

	private static void compareClasses(Class cleanroomClass, Class studentClass) {
	
		boolean equals = false;
		ArrayList<Method> studentMethods = new ArrayList<Method>(Arrays.asList(studentClass.getDeclaredMethods()));
		// checkMethods
		for(Method cleanroomMethod : cleanroomClass.getDeclaredMethods()){
			System.out.println(cleanroomMethod);
			for(Method studentMethod : studentMethods) {
				System.out.println(studentMethod);
				if(cleanroomMethod.equals(studentMethod)) {
					equals = true;
					studentMethods.remove(studentMethod);
					break;
				}

				if(equals){
					equals = false;
				}else{
					System.err.println("Method " +cleanroomMethod.getName() + " does not match or is not found");
				}
			}	
		}		
	}

	public static void main(String args[]){
		if(args == null || args.length != 2){
			usage();
		}

		Class cleanroomClass = null;
		Class studentClass = null;

		ClassLoader cl = ClassLoader.getSystemClassLoader();
		try{
			cleanroomClass = cl.loadClass(args[0]);
			studentClass = cl.loadClass(args[1]);

		} catch (ClassNotFoundException cnfe) {
			throw new Error("WARNING - " + cnfe.getMessage());
		}
		
		compareClasses(cleanroomClass,studentClass);

	}


}
