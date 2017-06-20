import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import tester.annotations.*;

@Exercises({ @Ex(exID = "GetDeclaredMethods", points = 2.0) })
public class UnitTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public static final PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout=100)
	@Points(exID = "GetDeclaredMethods", bonus = 1)
	public void testEmpty() { }
}

