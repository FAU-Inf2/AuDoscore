public interface IToTest {
	default int toTest_default() {
		return get_for_default__BUT_DIFFERENT_FROM_CLEAN();
	}

	static int toTest_static__BUT_DIFFERENT_FROM_CLEAN() { // does intentionally not match...
		return get_for_static();
	}

	private int get_for_default__BUT_DIFFERENT_FROM_CLEAN() { // irrelevant for @CompareInterface, because private!?
		return 24;
	}

	private static int get_for_static() {
		return 42;
	}
}
