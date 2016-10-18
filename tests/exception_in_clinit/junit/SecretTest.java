import static org.junit.Assert.*;
import org.junit.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	private static final ToTest tt = new ToTest(null);

	@Test(timeout = 666)
	@Points(exID = "clinit bug", bonus = 1)
	public void test2() {
		assertTrue(true);
	}
}
