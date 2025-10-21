public class ToTest {
	public static boolean toTest_boolean(boolean a, Boolean b) {
		return a && b; // @Replace should replace wrong student code "a || b" with expected code "a && b"
	}

	public static byte toTest_byte(byte a, Byte b) {
		return (byte) (a + b); // @Replace should replace wrong student code "a * b" with expected code "a + b"
	}

	public static char toTest_char(char a, Character b) {
		return (char) (a + b); // @Replace should replace wrong student code "a * b" with expected code "a + b"
	}

	public static short toTest_short(short a, Short b) {
		return (short) (a + b); // @Replace should replace wrong student code "a * b" with expected code "a + b"
	}

	public static int toTest_int(int a, Integer b) {
		return a + b; // @Replace should replace wrong student code "a * b" with expected code "a + b"
	}

	public static long toTest_long(long a, Long b) {
		return a + b; // @Replace should replace wrong student code "a * b" with expected code "a + b"
	}

	public static float toTest_float(float a, Float b) {
		return a + b; // @Replace should replace wrong student code "a * b" with expected code "a + b"
	}

	public static double toTest_double(double a, Double b) {
		return a + b; // @Replace should replace wrong student code "a * b" with expected code "a + b"
	}
}
