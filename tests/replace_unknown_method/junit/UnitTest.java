import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;

import tester.annotations.Ex;
import tester.annotations.Exercises;
import tester.annotations.Points;

@Exercises({ @Ex(exID = "ReplaceUnknownMethod", points = 2.0) })
public class UnitTest {
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public static final PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Timeout(value = 100, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "ReplaceUnknownMethod", bonus = 0.00001)
	public void testEmpty() { }
}

