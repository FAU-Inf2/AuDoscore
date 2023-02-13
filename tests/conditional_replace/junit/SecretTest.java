import static org.junit.Assert.assertTrue;
import org.junit.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();


	@Test(timeout = 500)
	@Replace(value = { "ToTest.replace" }, onlyIf = "field;ToTest.field;int[]")
	@Points(exID = "ConditionalReplace", bonus = 0.5)
	public void testSecret() {
		assertTrue(new ToTest(42).test());
	}
}

