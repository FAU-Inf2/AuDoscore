import static org.junit.Assert.assertEquals;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import tester.annotations.Points;
import tester.annotations.Replace;
import tester.annotations.SecretClass;

@SecretClass
public class SecretTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public static final PointsSummary pointsSummary = new PointsSummary();

	private static class Foo implements ToTest {
	}

	@Test(timeout=100)
	@Points(exID = "DefaultMethod", bonus = 0.5)
	@Replace({"ToTest.foo"})
	public void testFoo() {
		assertEquals("foo() is wrong", 42, new Foo().foo());
	}
}

