import org.junit.*;
import org.junit.runners.MethodSorters;
import tester.annotations.*;

import static org.junit.Assert.*;

@Exercises({ @Ex(exID = "GA4.6a", points = 12.5)})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UnitTest {
	// instead of explicitly coding the following rules here,
	// your test class can also just extend the class JUnitWithPoints
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout=100)
	@Points(exID = "GA4.6a", bonus = 8)
	public void test1() {
		assertEquals("Should return 42", 42, ToTest.toTest());
	}

	@Test(timeout=100)
	@Points(exID = "GA4.6a", bonus = 1.33)
	public void test2() {
		ToTest.exit();
	}

	@Test(timeout=100)
	@Points(exID = "GA4.6a", bonus = 4)
	public void test3() {
		assertEquals("Should return 42", 42, ToTest.toTest());
	}

}
