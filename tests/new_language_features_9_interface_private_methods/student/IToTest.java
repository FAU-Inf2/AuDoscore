public interface IToTest {
	default int toTest_default() {
		return get_for_default__BUT_DIFFERENT_FROM_CLEAN(); // @Replace should replace wrong student code "24" with expected code "42"
	}

	private int get_for_default__BUT_DIFFERENT_FROM_CLEAN() { // irrelevant for @CompareInterface, because private!?
		return 24;
	}
}
