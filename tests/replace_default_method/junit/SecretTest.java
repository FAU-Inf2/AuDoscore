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

	private static class Foo implements ToTest {
	}

	@Test
	@Timeout(value = 100, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "DefaultMethod", bonus = 0.5)
	@Replace({"ToTest.foo"})
	public void testFoo() {
		assertEquals(42, new Foo().foo(), "foo() is wrong");
	}
}

