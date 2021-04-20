import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import tester.annotations.*;

@Exercises({ @Ex(exID = "ConditionalReplace", points = 1) })
public class UnitTest {
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public final static PointsSummary pointsSummary = new PointsSummary();


	@Test
	@Timeout(value = 500, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "ConditionalReplace", bonus = 0.5)
	public void testEmpty() { }
}
