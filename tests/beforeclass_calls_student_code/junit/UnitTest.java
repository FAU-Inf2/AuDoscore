import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import tester.annotations.*;

@Exercises({ @Ex(exID = "PrintStackTrace", points = 1) })
public class UnitTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	private static int result = 0;

	@BeforeClass
	public static void init() {
		result = ToTest.test();
	}

	@Test(timeout = 500)
	@Points(exID = "PrintStackTrace", bonus = 1)
	public void test() {
		assertEquals(1, result);
	}
}

