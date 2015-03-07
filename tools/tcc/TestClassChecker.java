package tools.tcv;

import tester.annotations.*;

public class TestClassVerifier {
	
	private static void usage() {
		System.err.println("Specify 2 testclasses");
		System.exit(-1);
	}

	private static void checkClasses(String class1, String class2) {
		// load classes
		Class c1;
		Class c2;
		Exercises exercisesAnnotation1;
		Exercises exercisesAnnotation2;
		SecretClass secretClassAnnotation1;
		SecretClass secretClassAnnotation2;
		ClassLoader cl = ClassLoader.getSystemClassLoader();

		try { 
			c1 = cl.loadClass(class1);
			c2 = cl.loadClass(class2);
			exercisesAnnotation1 = (Exercises) c1.getAnnotation(Exercises.class);
			exercisesAnnotation2 = (Exercises) c2.getAnnotation(Exercises.class);
			
			secretClassAnnotation1 = (SecretClass) c1.getAnnotation(SecretClass.class);
			secretClassAnnotation2 = (SecretClass) c2.getAnnotation(SecretClass.class);
			
			// error cases
			if(secretClassAnnotation1 != null && secretClassAnnotation2 != null) {
				System.err.println("Found SECRETCLASS annotation in both testclasses");
				System.exit(-1);
			} else if (exercisesAnnotation1 != null && exercisesAnnotation2 != null) {
				if(secretClassAnnotation1 != null && exercisesAnnotation1 != null) {
					// c1 is secret ignoring ex annotation in c1
					System.err.println("EXERCISES annotation specified in secrettest[" + class1 +"], will take values from [" + class2+ "]");
					System.exit(2);
				} else if (secretClassAnnotation2 != null && exercisesAnnotation2 != null) {
					// c2 is secret ignoring ex annotation in c2
					System.err.println("EXERCISES annotation specified in secrettest[" + class2 +"], will take values from [" + class1+ "]");
					System.exit(1);
				}
			}

			if(secretClassAnnotation1 != null) {
				System.exit(2);
			}
			if(secretClassAnnotation2 != null) {
				System.exit(1);
			}

		} catch (ClassNotFoundException cnfe) {
			System.err.println(cnfe.getMessage());
		}
	}

	public static void main(String args[]) {
		if(args.length != 2) {
			usage();
		}

		String class1 = args[0];
		String class2 = args[1];
		checkClasses(class1, class2);
	}

}
