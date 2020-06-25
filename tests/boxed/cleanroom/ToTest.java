public class ToTest {
	public static boolean test(boolean a, boolean b) {
		return xor(a, a || b);
	}

	public static boolean xor(boolean a, boolean b) {
		return a != b;
	}
}

