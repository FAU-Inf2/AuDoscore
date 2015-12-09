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

	@Test(timeout=10001)
	@Points(exID = "GA4.6a", bonus = 47.11)
	public void test() {
	}
	@Test(timeout=10000)
	@Points(exID = "GA4.6a", bonus = 7.11)
	public void test2() {
	}
	@Test(timeout=5000)
	@Points(exID = "GA4.6a", bonus = 47)
	public void test3() {
	}
	@Test(timeout=4999)
	@Points(exID = "GA4.6a", bonus = 41)
	public void test4() {
	}
	@Test // XXX: no timeout here, (timeout=30000)
	@Points(exID = "GA4.6a", bonus = 4)
	public void test5() {
	}
}
