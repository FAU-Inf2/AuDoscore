import org.junit.*;
import tester.*;

import org.junit.*;
import org.junit.rules.*;
import static org.junit.Assert.*;
import java.lang.reflect.*;
import java.lang.*;
import java.util.*;
import java.io.*;

@tester.annotations.Exercises({ @tester.annotations.Ex(exID = "GA4.6a", points = 10.0)})
public class UnitTest {
	// instead of explicitly coding the following rules here,
	// your test class can also just extend the class JUnitWithPoints
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout=100)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test() {
		assertEquals("Should return 1", 1, ToTest.toTest());
	}

	@Test(timeout=100)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test2() {
		assertEquals("Should return 2", 2, ToTest.toTest2());
	}

	@Test(timeout=100)
	@Points(exID = "GA4.6a", malus = 0.0000001, bonus = 0.0000001)
	public void test3() {
		assertEquals("Should return 3", 3, ToTest.toTest3());
	}

	@Test(timeout=100)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test4() {
		assertEquals("Should return 4", 4, ToTest.toTest4());
	}
	
	@Test(timeout=100)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test5() {
		assertEquals("Should return 5", 5, ToTest.toTest5());
	}

	@Test(timeout=100)
	@Points(exID = "GA4.6a", malus = 0.0000001, bonus = 0.0000001)
	public void test6() {
		assertEquals("Should return 6", 6, ToTest.toTest6());
	}

	@Test(timeout=100)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test7() {
		assertEquals("Should return 7", 7, ToTest.toTest7());
	}

	@Test(timeout=100)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test8() {
		assertEquals("Should return 8", 8, ToTest.toTest8());
	}

	@Test(timeout=100)
	@Points(exID = "GA4.6a", bonus = 0.0000001)
	public void test9() {
		assertEquals("Should return 9", 9, ToTest.toTest9());
	}

	@Test(timeout=100)
	@Points(exID = "GA4.6a", malus = 0.0000001, bonus = 0.0000001)
	public void test10() {
		assertEquals("Should return 10", 10, ToTest.toTest10());
	}
}
