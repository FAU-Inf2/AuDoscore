import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import tester.annotations.Ex;
import tester.annotations.Exercises;
import tester.annotations.Points;

@Exercises({@Ex(exID = "ReplaceGeneric", points = 2)})
public class PublicTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public static final PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 666)
	@Points(exID = "ReplaceGeneric", bonus = 1)
	public void pubTest_no_op() {
	}
}
