import static org.junit.Assert.assertEquals;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import tester.annotations.*;

@SecretClass
public class SecretTest {
	// instead of explicitly coding the following rules here,
	// your test class can also just extend the class JUnitWithPoints
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@InitializeOnce("getValue")
	static int value;

	int getValue() {
		return 1;
	}

	@Test(timeout=500)
	@Points(exID = "InitOnce_Not_Static", bonus = 1.0)
	public void testSecret() {
		assertEquals(value, getValue());
	}
}

