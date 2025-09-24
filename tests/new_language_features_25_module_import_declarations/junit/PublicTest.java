import static org.junit.Assert.*;
import org.junit.*;
import tester.annotations.*;
import java.util.*;

@Exercises({@Ex(exID = "Module_Import_Declarations", points = 47.11)})
public class PublicTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 200)
	@Points(exID = "Module_Import_Declarations", bonus = 0.815, comment = "PublicTest: Should fail in \"vanilla\" because without @Replace.")
	public void pubTest() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42, ToTest.toTest());
	}
}
