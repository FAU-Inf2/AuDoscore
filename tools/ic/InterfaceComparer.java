public class InterfaceComparer {
	private static void usage() {
		System.err.println("java InterfaceComparer <CleanroomClass> <StudentClass>");
		System.exit(1);
	}

	private static compareClasses(Class cleanroomClass, Class studentClass){
	
		boolean equals = false;
		ArrayList<Method> studentMethods = new ArrayList<Method>(studentClass.getMethods());
		// checkMethods
		for(Method cleanroomMethod : cleanroomClass.getMethods()){
			for(Method studentMethod : studentMethods) {
				if(cleanroomMethod.equals(studentMethod)) {
					equals = true;
					studentMethods.remove(studentMethod);
					break;
				}

				if(equals){
					equals = false;
				}else{
					System.err.println("Method " +cleanroomMethod.getName() + "does not match or is not found");
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

		ClassLoader cl = ClassLoader.getSystemClassLoade();
		try{
			cleanroomClass = cl.loadClass(args[0]);
			studentClass = cl.loadClass(args[2]);

			compareClasses(cleanroomClass,studentClass);
		}

	}


}
