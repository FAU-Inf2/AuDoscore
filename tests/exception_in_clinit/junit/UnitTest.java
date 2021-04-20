import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import tester.annotations.*;

@Exercises({ @Ex(exID = "clinit bug", points = 4) })
public class UnitTest {
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "clinit bug", bonus = 0.0001)
	public void test() {}
}
