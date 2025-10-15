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
	@Points(exID = "Lambda", bonus = 0.815, comment = "SecretTest \"normal\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest")
	public void secTest() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", PublicTest.expected(), ToTest.toTest());
		assertEquals("Should pass in \"replaced\" because with @Replace now.", PublicTest.expected(), ToTest.toTest());
	}

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815, comment = "SecretTest \"regression\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest")
	public void secTest__regression() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", PublicTest.expected(), ToTest.toTestRegression());
	}
}
