import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public final static PointsSummary pointsSummary = new PointsSummary();

	private static final ToTest tt = new ToTest(null);

	@Test
	@Timeout(value = 500, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "clinit bug", bonus = 1)
	public void test2() {
		assertTrue(true);
	}
}
