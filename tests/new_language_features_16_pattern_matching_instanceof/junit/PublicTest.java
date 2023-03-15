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
	@Points(exID = "Lambda", bonus = 0.815, comment = "PublicTest \"simple\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_simple() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 4, ToTest.toTest_simple("Test"));
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42, ToTest.toTest_simple(42));
	}

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815, comment = "PublicTest \"moreComplex\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_moreComplex() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42, ToTest.toTest_moreComplex());
	}
}
