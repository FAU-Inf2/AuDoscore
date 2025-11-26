public class ToTest {
	public static int test() {
		try {
			throw new RuntimeException();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1;
	}
}

