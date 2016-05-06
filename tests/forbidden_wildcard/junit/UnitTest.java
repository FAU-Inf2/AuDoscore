import static org.junit.Assert.assertEquals;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

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
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout=200)
	@Points(exID = "FbWildcard", bonus = 47.11)
	public void test() {
		assertEquals("Should return 0", 0, ToTest.emptyList().size());
	}
}

