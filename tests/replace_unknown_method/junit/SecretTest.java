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
	@Timeout(value  =  100, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "ReplaceUnknownMethod", bonus = 1)
	@Replace({"ToTest.doesnotexist"})
	public void testBroken() { }
}

