import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;

import tester.annotations.Ex;
import tester.annotations.Exercises;
import tester.annotations.Forbidden;
import tester.annotations.NotForbidden;
import tester.annotations.Points;

@Exercises({ @Ex(exID = "NotForbiddenInArray", points = 2.0) })
@Forbidden({ "java.util." })
@NotForbidden({ "java.util.Random" })
public class UnitTest {
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public static final PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Timeout(value = 100, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "NotForbiddenInArray", bonus = 1)
	public void test() {
		assertEquals(0, ToTest.test());
	}
}

