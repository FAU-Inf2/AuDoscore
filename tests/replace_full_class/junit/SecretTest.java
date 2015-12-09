import static org.junit.Assert.assertEquals;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import tester.annotations.Points;
import tester.annotations.Replace;
import tester.annotations.SecretClass;

@SecretClass
public class SecretTest {
	// instead of explicitly coding the following rules here,
	// your test class can also just extend the class JUnitWithPoints
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout=200)
	@Points(exID = "GA4.6a", bonus = 47.11)
	@Replace({"ToTest.toTest2"}) // replacing wrong method intentionally
	public void test() {
		assertEquals("Should return 42", 42, ToTest.toTest());
	}

	@Test(timeout=200)
	@Points(exID = "GA4.6a", bonus = 23.00)
	@Replace({"ToTest"})
	public void test2() {
		assertEquals("Should return 23*42", 23*42, ToTest.toTest2() * ToTest.toTest());
	}
}
