import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;

import tester.annotations.Ex;
import tester.annotations.Exercises;
import tester.annotations.Forbidden;
import tester.annotations.NotForbidden;
import tester.annotations.Points;

@Forbidden({ "java." })
@NotForbidden({ "java.lang.Object", "java.lang.Integer" })
@Exercises({ @Ex(exID = "NotForbiddenInField", points = 12.5)})
public class UnitTest {

	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "NotForbiddenInField", bonus = 47.11)
	public void test() {
		final ToTest t = new ToTest();
		assertEquals(0, t.test());
	}
}

