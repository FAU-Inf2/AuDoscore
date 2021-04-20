import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;

import tester.annotations.Ex;
import tester.annotations.Exercises;
import tester.annotations.Forbidden;
import tester.annotations.NotForbidden;
import tester.annotations.Points;

@Exercises({ @Ex(exID = "FbWildcard", points = 42.0)})
@Forbidden({"java.util"})
@NotForbidden(
	value = {"java.util.*List"},
	type = Forbidden.Type.WILDCARD
)
public class UnitTest {
	// instead of explicitly coding the following rules here,
	// your test class can also just extend the class JUnitWithPoints
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Timeout(value = 200, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "FbWildcard", bonus = 47.11)
	public void test() {
		assertEquals(0, ToTest.emptyList().size(), "Should return 0");
	}
}

