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
	@Timeout(value = 300, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", bonus = 4)
	public void stdout() {
		ToTest.toTest(true);
	}

	@Test
	@Timeout(value = 300, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", bonus = 7)
	public void stderr() {
		ToTest.toTest(false);
	}
}
