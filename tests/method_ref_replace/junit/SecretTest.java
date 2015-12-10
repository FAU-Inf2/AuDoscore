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

	@Test(timeout=500)
	@Points(exID = "TestMethodRef", bonus = 0.5)
	@Replace({"ToTest.baz"})
	public void testFoo() {
		assertEquals("foo() is wrong", 42, new ToTest().foo());
	}

	@Test(timeout=500)
	@Points(exID = "TestMethodRef", bonus = 0.25)
	public void testBar() {
		assertEquals("bar() is wrong", 13, new ToTest().bar(() -> 13));
	}

	@Test(timeout=500)
	@Points(exID = "TestMethodRef", bonus = 0.25)
	public void testBaz() {
		assertEquals("baz() is wrong", 42, new ToTest().baz());
	}
}

