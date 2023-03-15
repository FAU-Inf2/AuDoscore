public class ToTest {
	public static int toTest() {
		return getSome();
	}

	@Deprecated(since = "Java-9", forRemoval = true)
	private static int getSome() {
		return 42; // @Replace should replace wrong student code "24" with expected code "42"
	}
}
