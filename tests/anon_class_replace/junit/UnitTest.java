import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;

import tester.annotations.Ex;
import tester.annotations.Exercises;
import tester.annotations.Points;

@Exercises({ @Ex(exID = "AnonClassReplace", points = 12.5)})
public class UnitTest {
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "AnonClassReplace", bonus = 0.1)
	public void testPublic() {
	}
}
