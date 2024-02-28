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
	@Points(exID = "Lambda", bonus = 0.815, comment = "SecretTest \"simple\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.getSome_simple")
	public void secTest_simple() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 4, ToTest.toTest_simple("Test"));
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42, ToTest.toTest_simple(42));
	}

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815, comment = "SecretTest \"moreComplex\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.getSome_moreComplex")
	public void secTest_moreComplex() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42, ToTest.toTest_moreComplex());
	}
}
