import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import tester.annotations.*;

@Exercises({ @Ex(exID = "ReplaceWithStatic", points = 1) })
public class UnitTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 100)
	@Points(exID = "ReplaceWithStatic", bonus = 0.1)
	public void test() { }
}

