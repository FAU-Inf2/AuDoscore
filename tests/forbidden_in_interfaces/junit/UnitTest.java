import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import tester.annotations.*;

@Exercises({ @Ex(exID = "ForbiddenInInterface", points = 12.5)})
public class UnitTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout=500)
	@Points(exID = "ForbiddenInInterface", bonus = 47.11)
	public void test() {
		Super x = new ToTest();
		x.test();
	}
}

