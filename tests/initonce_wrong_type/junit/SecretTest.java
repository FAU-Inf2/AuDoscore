import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;

import tester.annotations.*;

@SecretClass
public class SecretTest {
	// instead of explicitly coding the following rules here,
	// your test class can also just extend the class JUnitWithPoints
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public final static PointsSummary pointsSummary = new PointsSummary();

	@InitializeOnce("getValue")
	static String value;

	static int getValue() {
		return 2;
	}

	@Test
	@Timeout(value = 500, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "InitOnce_Wrong_Type", bonus = 1.0)
	public void testSecret() { }
}

