import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import tester.annotations.Ex;
import tester.annotations.Exercises;
import tester.annotations.Points;

@Exercises({ @Ex(exID = "GA4.6a", points = 12.5)})
public class UnitTest {
	// instead of explicitly coding the following rules here,
	// your test class can also just extend the class JUnitWithPoints
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout=300)
	@Points(exID = "GA4.6a", bonus = 4)
	public void stdout() {
		ToTest.toTest(true);
	}

	@Test(timeout=300)
	@Points(exID = "GA4.6a", bonus = 7)
	public void stderr() {
		ToTest.toTest(false);
	}
}
