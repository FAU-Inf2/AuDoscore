import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import tester.annotations.*;
import org.junit.jupiter.api.extension.RegisterExtension;

@Exercises({ @Ex(exID = "Locale", points = 1) })
public class UnitTest {

	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Timeout(value  =  500, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "Locale", bonus = 1)
	public void test() {
		assertEquals(42, ToTest.test());
	}
}

