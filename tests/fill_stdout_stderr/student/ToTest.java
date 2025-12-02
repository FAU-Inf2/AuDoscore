public class ToTest {
	public static int toTest(boolean stdout) {
		while (true) {
			if (stdout) {
				System.out.println("Flooding stdOut...");
			} else {
				System.err.println("Flooding stdErr...");
			}
		}
	}
}
