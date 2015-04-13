public class InterfaceComparer {
	private static void usage() {
		System.err.println("java InterfaceComparer <CleanroomClass> <StudentClass>");
		System.exit(1);
	}

	private static compareClasses(Class class1, Class class2){
	
		// checkMethods
		for(Method method : pub.getMethods()){
			// TODO	
		}		
	}



	public static void main(String args[]){
		if(args == null || args.length != 2){
			usage();
		}

		Class class1 = null;
		Class class2 = null;

		ClassLoader cl = ClassLoader.getSystemClassLoade();
		try{
			class1 = cl.loadClass(args[0]);
			class2 = cl.loadClass(args[2]);

			compareClasses(class1,class2);
		}

	}


}
