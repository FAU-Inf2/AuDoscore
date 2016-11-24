import static org.junit.Assert.assertEquals;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import tester.annotations.SecretClass;
import tester.annotations.Points;

@SecretClass
public class SecretTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public static final PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout=1000)
	@Points(exID = "ExtendsJFrame", bonus = 1)
	public void testSecret() {
		final ToTest toTest = new ToTest();
	}
}

