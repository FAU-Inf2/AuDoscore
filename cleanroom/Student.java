import java.util.ArrayList;
import java.lang.*;
import java.math.BigDecimal;
public class Student {
	int testVariable = 0;
	int __cleanVariable = 1;

	public int foo() {
		int ret = 4711 + testVariable;
		__cleanIteratorClass iterator = new __cleanIteratorClass();
		return ret + iterator.getCleanVar();
	}

	public double bar() {
		return 4711.0815 + testVariable;
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
		return "";
	}

	public static void recur(int i) {
		if(i > 0)
			recur(i-1);
	}
	
	public static void recur(int i, double d) {
		if(i > 0)
			recur(i-1);
	}

	public static void ioob() {
		int a[] = new int[10];
		a[a.length + 32]++;
	}

	public Student() {
	}

	public Student(int x) {
	}

	private class __cleanIteratorClass {
		public int getCleanVar() {
			return __cleanVariable - 23;
		}
	}
}

