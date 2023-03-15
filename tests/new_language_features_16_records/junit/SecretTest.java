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
	@Points(exID = "Lambda", bonus = 0.815, comment = "SecretTest \"regular\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("Point2D.sum")
	public void secTest_regular() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42 + 666, ToTest.toTest_regular(42, 666));
	}

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815, comment = "SecretTest \"member\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.getSum_member")
	public void secTest_member() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42 + 666, ToTest.toTest_member(42, 666, 4711));
	}

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815, comment = "SecretTest \"member_anonymous_inner\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.getSum_member_anonymous_inner")
	public void secTest_member_anonymous_inner() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42 + 666, ToTest.toTest_member_anonymous_inner(42, 666, 4711, 0x815));
	}
}
