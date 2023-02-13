public class ToTest {
	public static int test() {
		try {
			Thread.sleep(70000);
		} catch (InterruptedException e) {
			// Ignore
		}
		return 42;
	}
}

