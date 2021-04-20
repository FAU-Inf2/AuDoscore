import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;

import tester.annotations.Ex;
import tester.annotations.Exercises;
import tester.annotations.Points;

@Exercises({ @Ex(exID = "GA4.6a", points = 12.5)})
public class UnitTest {
	// instead of explicitly coding the following rules here,
	// your test class can also just extend the class JUnitWithPoints
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", bonus = 47, malus = 11)
	public void test() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", bonus = 47, malus = 11)
	public void test2() {
		assertEquals(23, ToTest.toTest2(), "Should return 23");
	}
}
