import org.junit.*;
import tester.annotations.*;

import static org.junit.Assert.*;

@SecretClass
public class SecretTest {
	// instead of explicitly coding the following rules here,
	// your test class can also just extend the class JUnitWithPoints
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();


	@Test(timeout=100)
	@Points(exID = "GA4.6a", bonus = 23.00)
	public void test2() {
		assertEquals("Should return 23", 23, ToTest.toTest2());
	}
}
