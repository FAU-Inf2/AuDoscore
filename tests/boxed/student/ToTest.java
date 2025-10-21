public class ToTest {
	public static Boolean toTest_boolean(Boolean a, boolean b) {
		return a || b; // @Replace should replace wrong student code "a || b" with expected code "a && b"
	}

	public static byte toTest_byte(Byte a, byte b) {
		return (byte) (a * b); // @Replace should replace wrong student code "a * b" with expected code "a + b"
	}

	public static char toTest_char(Character a, char b) {
		return (char) (a * b); // @Replace should replace wrong student code "a * b" with expected code "a + b"
	}

	public static short toTest_short(Short a, short b) {
		return (short) (a * b); // @Replace should replace wrong student code "a * b" with expected code "a + b"
	}

	public static int toTest_int(Integer a, int b) {
		return a * b; // @Replace should replace wrong student code "a * b" with expected code "a + b"
	}

	public static Long toTest_long(Long a, long b) {
		return a * b; // @Replace should replace wrong student code "a * b" with expected code "a + b"
	}

	public static float toTest_float(Float a, float b) {
		return a * b; // @Replace should replace wrong student code "a * b" with expected code "a + b"
	}

	public static double toTest_double(Double a, double b) {
		return a * b; // @Replace should replace wrong student code "a * b" with expected code "a + b"
	}
}
