import static org.junit.Assert.assertEquals;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import tester.annotations.Points;
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
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test11() {
		assertEquals("Should return 11", 11, ToTest.toTest11());
	}

	@Test(timeout=200)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test12() {
		assertEquals("Should return 12", 12, ToTest.toTest12());
	}

	@Test(timeout=200)
	@Points(exID = "GA4.6a", malus = 0.0000001, bonus = 0.0000001)
	public void test13() {
		assertEquals("Should return 13", 13, ToTest.toTest13());
	}

	@Test(timeout=200)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test14() {
		assertEquals("Should return 14",14, ToTest.toTest14());
	}
	
	@Test(timeout=200)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test15() {
		assertEquals("Should return 15", 15, ToTest.toTest15());
	}

	@Test(timeout=200)
	@Points(exID = "GA4.6a", malus = 0.0000001, bonus = 0.0000001)
	public void test16() {
		assertEquals("Should return 16", 16, ToTest.toTest16());
	}

	@Test(timeout=200)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test17() {
		assertEquals("Should return 17", 17, ToTest.toTest17());
	}

	@Test(timeout=200)
	@Points(exID = "GA4.6a", bonus = 0.0000001, malus = 0.0000001)
	public void test18() {
		assertEquals("Should return 18", 18, ToTest.toTest18());
	}

	@Test(timeout=200)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test19() {
		assertEquals("Should return 19", 19, ToTest.toTest19());
	}

	@Test(timeout=200)
	@Points(exID = "GA4.6a", malus = 0.0000001, bonus = 0.0000001)
	public void test20() {
		assertEquals("Should return 20", 20, ToTest.toTest20());
	}
}
