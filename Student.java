public class Student {
	int testVariable = 0;
//	ClassLoader l;
	private int fooo;

	public int foo() {
		return 4711 + 1;
	}

	public double bar() {
		return 4711.0815;
	}

	public String baz() {
		return "I am nice.";
	}

	public String foobar() {
		double ff = Math.abs(1.2322);
		return "I am dangerous.";
	}

	public static Object getNull() {
		java.util.LinkedList<Integer> ll = new java.util.LinkedList<>();
		return null;
	}

	public static String doNull() {
		//java.util.ArrayList<Integer> ll = new java.util.ArrayList<>();
		return getNull().toString();
	}

	public static void recur() {
		recur();
	}

	public static void ioob() {
		int a[] = new int[10];
		a[a.length + 32]++;
	}

	public Student() {
	}

	public Student(int x) {
		System.out.println(x);
	}
}
