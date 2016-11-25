import static org.junit.Assert.assertEquals;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import tester.annotations.Ex;
import tester.annotations.Exercises;
import tester.annotations.Points;

@Exercises({ @Ex(exID = "NotForbiddenInArray", points = 2.0) })
@Forbidden({ "java.util." })
@NotForbidden({ "java.util.Random" })
public class UnitTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public static final PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout=100)
	@Points(exID = "NotForbiddenInArray", bonus = 1)
	public void test() {
		assertEquals(0, ToTest.test());
	}
}

