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
	@Points(exID = "Lambda", bonus = 0.815, comment = "secTest_toTest_getSome: Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.getSome")
	public void secTest_toTest_getSome() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 2 + 3 + 4 + 5, ToTest.toTest_getSome());
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 2 + 3 + 4 + 5, ToTest.toTest_getSome());
	}

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815, comment = "secTest_toTest_getSome_int: Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.getSome")
	public void secTest_toTest_getSome_int() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 2 + 3 + 4 + 5, new ToTest().toTest_getSome_int(666));
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 2 + 3 + 4 + 5, new ToTest().toTest_getSome_int(666));
	}
}
