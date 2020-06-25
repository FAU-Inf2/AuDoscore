public class ToTest {
	int field;

	public ToTest(final int x) {
		this.field = x;
	}

	public int replace() {
		return 5;
	}

	public boolean test() {
		return this.replace() == 42;
	}
}

