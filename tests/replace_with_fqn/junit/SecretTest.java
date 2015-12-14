import static org.junit.Assert.assertEquals;

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
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout=100)
	@Replace({ "ToTest.test" })
	@Points(exID = "FQN", bonus = 47.11)
	public void testSec() {
		assertEquals(1, new ToTest().test(java.util.Arrays.asList(42)));
	}
}
