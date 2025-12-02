public class ToTest {
	public static int toTest() {
		try {
			Class<?> cls = Class.forName("UnitTest");
			java.lang.reflect.Method m;
			m = cls.getDeclaredMethod("test");
			String s = m.getName();
			System.out.println(s);
		} catch (Throwable ignored) {
		}
		return 42;
	}
}
