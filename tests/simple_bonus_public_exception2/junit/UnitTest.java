import org.junit.*;
import tester.*;

import org.junit.*;
import org.junit.rules.*;
import static org.junit.Assert.*;
import java.lang.reflect.*;
import java.lang.*;
import java.util.*;
import java.io.*;

@Exercises({ @Ex(exID = "GA4.6a", points = 12.5)})
public class UnitTest {
	// instead of explicitly coding the following rules here,
	// your test class can also just extend the class JUnitWithPoints
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout=100)
	@Bonus(exID = "GA4.6a", bonus = 47.11)
	public void test() {
		// FIXME: in real life, we would never write this that way!
		assertTrue("Should return 42", ToTest.toTest().equals(new Integer(42)));
	}
}
