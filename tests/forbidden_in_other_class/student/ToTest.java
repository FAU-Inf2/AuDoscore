public class ToTest {
	public static int test() {
		return Exploit.test();
	}
}

class Exploit {
	public static int test() {
		return Integer.parseInt("0");
	}
}

