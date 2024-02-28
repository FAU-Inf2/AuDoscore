public interface IToTest {
	default int toTest_default() {
		return get_for_default();
	}

	static int toTest_static() {
		return get_for_static();
	}

	private int get_for_default() {
		return 24;
	}

	private static int get_for_static() {
		return 42;
	}
}
