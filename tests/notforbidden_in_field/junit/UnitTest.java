import static org.junit.Assert.assertEquals;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import tester.annotations.Ex;
import tester.annotations.Exercises;
import tester.annotations.Points;

@Forbidden({ "java." })
@NotForbidden({ "java.lang.Object", "java.lang.Integer" })
@Exercises({ @Ex(exID = "NotForbiddenInField", points = 12.5)})
public class UnitTest {

	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout=200)
	@Points(exID = "NotForbiddenInField", bonus = 47.11)
	public void test() {
		final ToTest t = new ToTest();
		assertEquals(0, t.test());
	}
}

