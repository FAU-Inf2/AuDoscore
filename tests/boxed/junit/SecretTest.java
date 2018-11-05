import static org.junit.Assert.*;

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
	@Points(exID = "Boxed", bonus = 1)
	@Replace({ "ToTest.test", "ToTest.xor" })
	public void testReplace() {
		assertEquals(true, ToTest.test(true, true));
		assertEquals(false, ToTest.test(true, false));
	}
}

