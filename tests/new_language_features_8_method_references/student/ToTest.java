import java.util.function.Function;

public class ToTest {
	// ========== static: class method reference ==========
	public static int toTest_static() {
		return evaluate_static(ToTest::get42_static);
	}

	private static int evaluate_static(Function<Integer, Integer> f) {
		return f.apply(666);
	}

	private static int get42_static(int x) {
		return 24; // @Replace should replace wrong student code "24" with expected code "42"
	}

	// ========== non-static: instance method reference ==========
	public int toTest() {
		return evaluate(this::get42);
	}

	private int evaluate(Function<Integer, Integer> f) {
		return f.apply(666);
	}

	private int get42(int x) {
		return 24; // @Replace should replace wrong student code "24" with expected code "42"
	}
}
