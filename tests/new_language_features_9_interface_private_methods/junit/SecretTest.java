import static org.junit.Assert.*;
import org.junit.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 666)
	@Points(exID = "Interface Private Methods", bonus = 1, comment = "SecretTest \"default method\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("IToTest.toTest_default")
	public void secTest() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42, new ToTest().toTest_default());
	}
}
