import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public final static PointsSummary pointsSummary = new PointsSummary();


	@Test
	@Timeout(value = 500, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Replace(value = { "ToTest.replace" }, onlyIf = "field;ToTest.field;int[]")
	@Points(exID = "ConditionalReplace", bonus = 0.5)
	public void testSecret() {
		assertTrue(new ToTest(42).test());
	}
}

