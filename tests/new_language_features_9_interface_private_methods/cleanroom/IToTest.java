public interface IToTest {
	default int toTest_default() {
		return 42; // @Replace should replace wrong student code "24" with expected code "42"
	}
}
