public class ToTest {
	public static <E extends Number> int test(E e) {
		return e.intValue();
	}

	public static <E extends Number> int test2(E[] e) {
		return e.length;
	}
}
