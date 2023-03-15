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
	@Points(exID = "Lambda", bonus = 0.815, comment = "SecretTest \"static\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.get42_static")
	public void secTest__static() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42, ToTest.toTest_static());
	}

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815, comment = "SecretTest \"non-static\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.get42")
	public void secTest__nonstatic() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42, new ToTest().toTest());
	}
}
