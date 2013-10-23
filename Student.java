public class Student {
	int testVariable = 0;
//	ClassLoader l;

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
		return "I am dangerous.";
	}

	public static Object getNull() {
		return null;
	}

	public static String doNull() {
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
