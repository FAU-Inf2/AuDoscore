import static org.junit.Assert.*;
import org.junit.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "Lambda", points = 47.11)})
public class PublicTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815, comment = "PublicTest: Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_toTest_getSome() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 2 + 3 + 4 + 5, ToTest.toTest_getSome());
	}

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815, comment = "PublicTest: Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_toTest_getSome_int() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 2 + 3 + 4 + 5, new ToTest().toTest_getSome_int(666));
	}
}
