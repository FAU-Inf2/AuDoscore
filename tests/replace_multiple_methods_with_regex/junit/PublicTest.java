import static org.junit.Assert.assertEquals;
import org.junit.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "ReplaceMultipleWithRegex", points = 1)})
public class PublicTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 100)
	@Points(exID = "ReplaceMultipleWithRegex", bonus = 0.1, comment = "PublicTest: Should fail in \"vanilla\" because without @Replace.")
	public void pubTest() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42 + 42, ToTest.toTest_alpha() + new ToTest().toTest_beta());
	}
}

