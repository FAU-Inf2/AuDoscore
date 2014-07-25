public class ToTest {
	public static int toTest(boolean stdout) {
		for (int i = 0; i < 100000; i++) {
			while (true) {
				if (stdout) {
					System.out.println("Floodfill stdout...");
				} else {
					System.err.println("Floodfill stderr...");
				}
			}
		}
		return 42;
	}
}
