public class ToTest {
	public static Boolean test(Boolean a, Boolean b) {
		return xor(a, a || b);
	}

	public static Boolean xor(Boolean a, Boolean b) {
		return a != b;
	}
}

