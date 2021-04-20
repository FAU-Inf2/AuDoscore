import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;

import tester.annotations.Points;
import tester.annotations.Replace;
import tester.annotations.SecretClass;

@SecretClass
public class SecretTest {
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public static final PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Timeout(value = 100, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "ReplaceGeneric4", bonus = 1)
	@Replace({"ToTest.test"})
	public void testFoo() {
		final ToTest<Integer> toTest = new ToTest<Integer>(1);
		assertEquals(0, toTest.test(1));
	}
}

