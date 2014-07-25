import org.junit.*;
import tester.*;

import org.junit.*;
import org.junit.rules.*;
import static org.junit.Assert.*;
import java.lang.reflect.*;
import java.lang.*;
import java.util.*;
import java.io.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

@tester.annotations.Exercises({ @tester.annotations.Ex(exID = "GA4.6a", points = 12.5)})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UnitTest {
	// instead of explicitly coding the following rules here,
	// your test class can also just extend the class JUnitWithPoints
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout=100)
	@tester.annotations.Bonus(exID = "GA4.6a", bonus = 8)
	public void test1() {
		assertEquals("Should return 42", 42, ToTest.toTest());
	}

	@Test(timeout=100)
	@tester.annotations.Bonus(exID = "GA4.6a", bonus = 1.33)
	public void test2() {
		ToTest.exit();
	}

	@Test(timeout=100)
	@tester.annotations.Bonus(exID = "GA4.6a", bonus = 4)
	public void test3() {
		assertEquals("Should return 42", 42, ToTest.toTest());
	}

}
