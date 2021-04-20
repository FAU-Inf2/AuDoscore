import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;

import tester.annotations.Points;
import tester.annotations.Replace;
import tester.annotations.SecretClass;

@SecretClass
public class SecretTest {
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public static final PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Timeout(value = 1000, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "TestMethodRef", bonus = 0.5)
	@Replace({"ToTest.baz"})
	public void testFoo() {
		assertEquals(42, new ToTest().foo(), "foo() is wrong");
	}

	@Test
	@Timeout(value = 1000, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "TestMethodRef", bonus = 0.25)
	public void testBar() {
		assertEquals(13, new ToTest().bar(() -> 13), "bar() is wrong");
	}

	@Test
	@Timeout(value = 1000, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "TestMethodRef", bonus = 0.25)
	public void testBaz() {
		assertEquals(42, new ToTest().baz(), "baz() is wrong");
	}
}

