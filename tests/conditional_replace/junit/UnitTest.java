import org.junit.*;
import tester.annotations.*;

@Exercises({ @Ex(exID = "ConditionalReplace", points = 1) })
public class UnitTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();


	@Test(timeout = 500)
	@Points(exID = "ConditionalReplace", bonus = 0.5)
	public void testEmpty() { }
}
