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

	@Test(timeout=200)
	@Points(exID = "AnonClassReplace2", bonus = 47.11)
	@Replace({"ToTest.get"})
	public void testSecret() {
		assertEquals("Should return 0", 0, ToTest.get(42, 42));
	}
}

