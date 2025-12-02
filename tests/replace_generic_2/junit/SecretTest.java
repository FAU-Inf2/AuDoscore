import static org.junit.Assert.assertEquals;

import java.util.Collections;

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
	@Points(exID = "ReplaceGeneric2", bonus = 1)
	@Replace({"ToTest.get2"})
	public void testFoo() {
		final ToTest<Integer> toTest = new ToTest<Integer>();
		assertEquals(Integer.valueOf(1), toTest.get(Collections.singletonList(1)));
	}
}

