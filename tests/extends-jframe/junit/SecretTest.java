import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;

import tester.annotations.SecretClass;
import tester.annotations.Points;

@SecretClass
public class SecretTest {
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public static final PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Timeout(value = 1000, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "ExtendsJFrame", bonus = 1)
	public void testSecret() {
		final ToTest toTest = new ToTest();
	}
}

