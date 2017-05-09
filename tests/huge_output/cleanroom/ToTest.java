public class ToTest {
	public static String foo() {
		final StringBuilder resultBuilder = new StringBuilder();
		for (int i = 0; i < 0x1000; ++i) {
			resultBuilder.append((char) ('a' + i % 26));
		}
		return resultBuilder.toString();
	}

	public static String bar() {
		return "a";
	}

	public static String baz() {
		final StringBuilder resultBuilder = new StringBuilder();
		for (int i = 0; i < 0x1000; ++i) {
			resultBuilder.append((char) ('a' + i % 26));
		}
		return resultBuilder.toString();
	}
}

