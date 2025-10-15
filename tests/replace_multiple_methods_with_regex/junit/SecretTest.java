import static org.junit.Assert.assertEquals;
import org.junit.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 500)
	@Points(exID = "ReplaceMultipleWithRegex", bonus = 1, comment = "SecretTest: Should pass in \"replaced\" because with @Replace now.")
	@Replace({"ToTest.toTest_.*"})
	public void secTest() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42 + 42, ToTest.toTest_alpha() + new ToTest().toTest_beta());
	}
}
