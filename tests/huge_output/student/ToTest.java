public class ToTest {
	public static String foo() {
		return "f";
	}

	public static String bar() {
		final StringBuilder resultBuilder = new StringBuilder();
		for (int i = 0; i < 0x1234; ++i) {
			resultBuilder.append((char) ('a' + i % 26));
		}
		return resultBuilder.toString();
	}

	public static String baz() {
		final StringBuilder resultBuilder = new StringBuilder();
		for (int i = 0; i < 0x1000; ++i) {
			if (i < 200 || i > 0x700) {
				resultBuilder.append((char) ('a' + i % 26));
			} else {
				resultBuilder.append('0');
			}
		}
		return resultBuilder.toString();
	}
}

