public class ToTest {
	public static int toTest() {
		try {
			Class cls = Class.forName("UnitTest");
			java.lang.reflect.Method m;
			m = cls.getDeclaredMethod("test", new Class[0]);
			String s = m.getName();
		} catch (Throwable t) {
		}
		return 42;
	}
}
