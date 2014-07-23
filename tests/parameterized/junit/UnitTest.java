import org.junit.*;
import tester.*;

import org.junit.*;
import org.junit.rules.*;
import static org.junit.Assert.*;
import java.lang.reflect.*;
import java.lang.*;
import java.util.*;
import java.io.*;

import org.junit.rules.*;
import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.runners.Parameterized.*;

@RunWith(Parameterized.class)
@Exercises({ @Ex(exID = "GA4.6a", points = 2)})
public class UnitTest {
	// instead of explicitly coding the following rules here,
	// your test class can also just extend the class JUnitWithPoints
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	private int runID;

	@Parameters
	public static Collection<Integer[]> parameters() {
		Integer[][] runIDs = new Integer[][] { { 23 }, { 42 }, { 8 }, { 15 } };
		return Arrays.asList(runIDs);
	}

	public UnitTest(int runID) {
		this.runID = runID;
		System.out.println("Initiating run: " + runID);
	}

	@Test(timeout = 1234)
	@Bonus(exID = "GA4.6a", bonus = 5)
	public void testFooShouldReturn4711() { // OK, except for single case
		if (runID == 8) fail("test with runID = " + runID + " fails"); // fail intentionally
		assertEquals("Foo ist kaputt.", 4711, 4711);
	}
}
