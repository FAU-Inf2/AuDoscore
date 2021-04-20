import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;
import tester.annotations.*;

@Exercises({ @Ex(exID = "ForbiddenInInterface", points = 12.5)})
public class UnitTest {
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Timeout(value = 500, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "ForbiddenInInterface", bonus = 47.11)
	public void test() {
		Super x = new ToTest();
		x.test();
	}
}

