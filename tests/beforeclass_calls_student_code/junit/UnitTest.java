import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;
import tester.annotations.*;

@Exercises({ @Ex(exID = "PrintStackTrace", points = 1) })
public class UnitTest {
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public final static PointsSummary pointsSummary = new PointsSummary();

	private static int result = 0;

	@BeforeAll
	public static void init() {
		result = ToTest.test();
	}

	@Test
	@Timeout(value = 500, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "PrintStackTrace", bonus = 1)
	public void test() {
		assertEquals(1, result);
	}
}

