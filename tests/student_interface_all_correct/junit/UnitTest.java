import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;

import tester.annotations.CompareInterface;
import tester.annotations.Ex;
import tester.annotations.Exercises;
import tester.annotations.Points;

@Exercises({ @Ex(exID = "GA4.6a", points = 10.0)})
@CompareInterface({"ToTest"})
public class UnitTest {
	// instead of explicitly coding the following rules here,
	// your test class can also just extend the class JUnitWithPoints
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test() {
		assertEquals(1, ToTest.toTest(), "Should return 1");
	}

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test2() {
		assertEquals(2, ToTest.toTest2(), "Should return 2");
	}

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test3() {
		assertEquals(3, ToTest.toTest3(), "Should return 3");
	}

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test4() {
		assertEquals(4, ToTest.toTest4(), "Should return 4");
	}
	
	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test5() {
		assertEquals(5, ToTest.toTest5(), "Should return 5");
	}

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test6() {
		assertEquals(6, ToTest.toTest6(), "Should return 6");
	}

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test7() {
		assertEquals(7, ToTest.toTest7(), "Should return 7");
	}

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test8() {
		assertEquals(8, ToTest.toTest8(), "Should return 8");
	}

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test9() {
		assertEquals(9, ToTest.toTest9(), "Should return 9");
	}

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test10() {
		assertEquals(10, ToTest.toTest10(), "Should return 10");
	}
}
