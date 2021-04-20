import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;

import tester.annotations.*;

@SecretClass
@SafeCallers({ "SecretTest" })
public class SecretTest {
	// instead of explicitly coding the following rules here,
	// your test class can also just extend the class JUnitWithPoints
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public final static PointsSummary pointsSummary = new PointsSummary();

	@InitializeOnce("getValue")
	static int value;

	static int getValue() {
		return ToTest.get();
	}

	@Test
	@Timeout(value = 500, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "InitOnce_Exception", bonus = 1.0)
	public void testSecret() {
		assertTrue(value == 10);
	}
}

