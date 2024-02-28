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
	@Points(exID = "Lambda", bonus = 0.815, comment = "PublicTest \"static\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__static() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42, ToTest.toTest_static());
	}

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815, comment = "PublicTest \"non-static\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__nonstatic() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42, new ToTest().toTest());
	}
}
