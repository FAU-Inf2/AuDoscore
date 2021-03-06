import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import tester.annotations.Points;
import tester.annotations.Replace;
import tester.annotations.SecretClass;

@SecretClass
public class SecretTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public static final PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 100)
	@Points(exID = "ReplaceUnknownMethod", bonus = 1)
	@Replace({"ToTest.doesnotexist"})
	public void testBroken() { }
}

