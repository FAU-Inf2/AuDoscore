import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;

import tester.annotations.Points;
import tester.annotations.SecretClass;

@SecretClass
public class SecretTest {
	// instead of explicitly coding the following rules here,
	// your test class can also just extend the class JUnitWithPoints
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test11() {
		assertEquals(11, ToTest.toTest11(), "Should return 11");
	}

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test12() {
		assertEquals(12, ToTest.toTest12(), "Should return 12");
	}

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", malus = 0.0000001, bonus = 0.0000001)
	public void test13() {
		assertEquals(13, ToTest.toTest13(), "Should return 13");
	}

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test14() {
		assertEquals(14, ToTest.toTest14(), "Should return 14");
	}
	
	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test15() {
		assertEquals(15, ToTest.toTest15(), "Should return 15");
	}

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", malus = 0.0000001, bonus = 0.0000001)
	public void test16() {
		assertEquals(16, ToTest.toTest16(), "Should return 16");
	}

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test17() {
		assertEquals(17, ToTest.toTest17(), "Should return 17");
	}

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", bonus = 0.0000001, malus = 0.0000001)
	public void test18() {
		assertEquals(18, ToTest.toTest18(), "Should return 18");
	}

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test19() {
		assertEquals(19, ToTest.toTest19(), "Should return 19");
	}

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", malus = 0.0000001, bonus = 0.0000001)
	public void test20() {
		assertEquals(20, ToTest.toTest20(), "Should return 20");
	}
}
