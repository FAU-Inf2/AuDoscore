import static org.junit.Assert.*;
import org.junit.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815)
	// @Replace("ToTest.toTest_default") // TODO: @Replace gives "INTERNAL ERROR" if student does NOT also @Override this method!
	public void secTest() {
		assertEquals("Should return 42.", 42, new ToTest().toTest_default());
	}
}
