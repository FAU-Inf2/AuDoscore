public interface IToTest {
	default int getSome_default() {
		return 24;
	}

	static int getSome_static() {
		return 42;
	}
}
