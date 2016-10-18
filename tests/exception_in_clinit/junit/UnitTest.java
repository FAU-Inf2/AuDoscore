import org.junit.*;
import tester.annotations.*;

@Exercises({ @Ex(exID = "clinit bug", points = 4) })
public class UnitTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 200)
	@Points(exID = "clinit bug", bonus = 0.0001)
	public void test() {}
}
