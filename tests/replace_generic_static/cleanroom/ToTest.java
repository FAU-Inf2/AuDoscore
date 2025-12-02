public class ToTest {
	public static <T extends Number> int test(T t) {
		return t.intValue();
	}

	public static <T extends Number> int test2(T[] t) {
		return t.length;
	}
}
