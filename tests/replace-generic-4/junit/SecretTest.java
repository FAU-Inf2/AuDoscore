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
	public static final PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout=100)
	@Points(exID = "ReplaceGeneric4", bonus = 1)
	@Replace({"ToTest.test"})
	public void testFoo() {
		final ToTest<Integer> toTest = new ToTest<Integer>(1);
		assertEquals(0, toTest.test(1));
	}
}

