import static org.junit.Assert.assertEquals;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import tester.annotations.CompareInterface;
import tester.annotations.Exercises;
import tester.annotations.Points;

@Exercises({ @tester.annotations.Ex(exID = "GA4.6a", points = 12.5)})
@CompareInterface({"ToTest.foo","ToTest.bar"})
public class UnitTest {
	// instead of explicitly coding the following rules here,
	// your test class can also just extend the class JUnitWithPoints
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout=100)
	@Points(exID = "GA4.6a", bonus = 47)
	public void test() {
		assertEquals("Should return 42", 42, ToTest.toTest());
	}

	@Test(timeout=100)
	@Points(exID = "GA4.6a", malus = 11)
	public void test2() {
		assertEquals("Should return 42", 42, ToTest.toTest());
	}
}
