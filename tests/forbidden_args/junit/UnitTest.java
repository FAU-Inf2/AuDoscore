import org.junit.*;
import tester.*;
import tester.annotations.*;

import org.junit.rules.*;
import static org.junit.Assert.*;
import java.lang.reflect.*;
import java.util.*;
import java.io.*;

@tester.annotations.Exercises({ @tester.annotations.Ex(exID = "GA4.6a", points = 12.5)})
@tester.annotations.Forbidden({"java.lang."})
@tester.annotations.NotForbidden({"java.lang.String", "java.lang.Object"})
public class UnitTest {
	// instead of explicitly coding the following rules here,
	// your test class can also just extend the class JUnitWithPoints
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout=100)
	@tester.annotations.Bonus(exID = "GA4.6a", bonus = 47.11)
	public void test() {
		assertEquals("Should return 42", 42, ToTest.toTest());
	}
}
