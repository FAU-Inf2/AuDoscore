public class ToTest {
	int[] field;

	public ToTest(final int x) {
		this.field = new int[] { x };
	}

	public int replace() {
		return this.field[0];
	}

	public boolean test() {
		return this.replace() == 42;
	}
}

