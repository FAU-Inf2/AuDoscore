import org.junit.*;
import tester.*;

import org.junit.*;
import org.junit.rules.*;
import static org.junit.Assert.*;
import java.lang.reflect.*;
import java.lang.*;
import java.util.*;
import java.io.*;

@tester.annotations.Exercises({ @tester.annotations.Ex(exID = "GA4.6a", points = 12.5)})
public class UnitTest {
	// instead of explicitly coding the following rules here,
	// your test class can also just extend the class JUnitWithPoints
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout=10001)
	@tester.annotations.Bonus(exID = "GA4.6a", bonus = 47.11)
	public void test() {
	}
	@Test(timeout=10000)
	@tester.annotations.Bonus(exID = "GA4.6a", bonus = 7.11)
	public void test2() {
	}
	@Test(timeout=5000)
	@tester.annotations.Bonus(exID = "GA4.6a", bonus = 47)
	public void test3() {
	}
	@Test(timeout=5000)
	@tester.annotations.Bonus(exID = "GA4.6a", bonus = 41)
	public void test4() {
	}
	@Test(timeout=30000)
	@tester.annotations.Bonus(exID = "GA4.6a", bonus = 4)
	public void test5() {
	}
}
