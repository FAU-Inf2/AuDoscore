import static org.junit.Assert.*;
import org.junit.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "Interface Private Methods", points = 42)})
@CompareInterface({"IToTest", "ToTest"})
public class PublicTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 666)
	@Points(exID = "Interface Private Methods", bonus = 1, comment = "PublicTest \"default method\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42, new ToTest().toTest_default());
	}
}
