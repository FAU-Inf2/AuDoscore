import org.junit.*;
import tester.annotations.*;

import static org.junit.Assert.*;

@Exercises({ @Ex(exID = "GA4.6a", points = 12.5)})
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
		assertEquals("Should return 42", 42, new ToTest().toTest());
	}

	@Test(timeout=100)
	@Points(exID = "GA4.6a", malus = 11)
	public void test2() {
		assertEquals("Should return true", true, new ToTest2().toTest2());
	}
}
