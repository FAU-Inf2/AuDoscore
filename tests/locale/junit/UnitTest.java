import static org.junit.Assert.assertEquals;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import tester.annotations.*;

@Exercises({ @Ex(exID = "Locale", points = 1) })
public class UnitTest {

	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 500)
	@Points(exID = "Locale", bonus = 1)
	public void test() {
		assertEquals(42, ToTest.test());
	}
}

