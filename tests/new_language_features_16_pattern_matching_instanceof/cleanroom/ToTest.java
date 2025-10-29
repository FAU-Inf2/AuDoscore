public class ToTest {
	// ========== simple ==========
	public static int toTest_simple(Object some) {
		return getSome_simple(some);
	}

	private static <T> int getSome_simple(T some) {
		int result = 0;
		if (some instanceof String string) {
			result = string.length();
		} else if (some instanceof Integer integer) {
			result = integer;
		}
		return result; // @Replace should replace wrong student code "return result + 1;" with expected code "return result;"
	}

	// ========== more complex ==========
	public static int toTest_moreComplex() {
		return getSome_moreComplex(new C());
	}

	private static <T> int getSome_moreComplex(T some) {
		int result = 0;
		if (some instanceof A a && a instanceof B b && b instanceof C c) {
			result = c.getSome();
		} else if (some instanceof Integer integer) {
			result = integer;
		}
		return result; // @Replace should replace wrong student code "return result + 1;" with expected code "return result;"
	}
}
