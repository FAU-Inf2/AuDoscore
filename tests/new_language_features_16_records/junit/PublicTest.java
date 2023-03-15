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
	@Points(exID = "Lambda", bonus = 0.815, comment = "PublicTest \"regular\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_regular() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42 + 666, ToTest.toTest_regular(42, 666));
	}

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815, comment = "PublicTest \"member\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_member() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42 + 666, ToTest.toTest_member(42, 666, 4711));
	}

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815, comment = "PublicTest \"member_anonymous_inner\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_member_anonymous_inner() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42 + 666, ToTest.toTest_member_anonymous_inner(42, 666, 4711, 0x815));
	}
}
