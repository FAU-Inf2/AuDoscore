import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import tester.annotations.*;

@Exercises({ @Ex(exID = "SafeCallers", points = 2.0) })
@SafeCallers({ "UnitTestBase" })
public class UnitTest extends UnitTestBase {

	private static ClassLoader cl;

	static {
		try {
			cl = new java.net.URLClassLoader(new java.net.URL[] { new java.io.File(".").toURL() }, null);
		} catch (final Throwable t) {
			cl = null;
		}
	}

	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public static final PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout=100)
	@Points(exID = "SafeCallers", bonus = 1)
	public void test() {
		super.check(cl);
	}
}

class UnitTestBase {
	public void check(ClassLoader cl) {
		try {
			final Method m = Class.forName("ToTest", true, cl).getDeclaredMethod("get");
			assertEquals(Integer.valueOf(0), m.invoke(null));
		} catch (final Throwable t) {
			throw new RuntimeException(t);
		}
	}
}
