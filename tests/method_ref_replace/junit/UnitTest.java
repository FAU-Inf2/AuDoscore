import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;

import tester.annotations.Ex;
import tester.annotations.Exercises;
import tester.annotations.Points;

@Exercises({ @Ex(exID = "TestMethodRef", points = 2.0) })
public class UnitTest {
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public static final PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Timeout(value = 1000, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "TestMethodRef", bonus = 0.00001)
	public void testInterfaceFoo() {
		new ToTest().foo();
	}

	@Test
	@Timeout(value = 1000, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "TestMethodRef", bonus = 0.00001)
	public void testInterfaceBar() {
		new ToTest().bar(() -> 13);
	}

	@Test
	@Timeout(value = 1000, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "TestMethodRef", bonus = 0.00001)
	public void testInterfaceBaz() {
		final int i = new ToTest().baz();
	}
}

