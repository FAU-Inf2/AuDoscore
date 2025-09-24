import static org.junit.Assert.*;
import org.junit.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "UnnamedPatterns", points = 47.11)})
public class PublicTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 200)
	@Points(exID = "UnnamedPatterns", bonus = 0.815, comment = "PublicTest \"toTest_unnamed_patterns__elide_type_patterns\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest_unnamed_patterns__elide_type_patterns() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42, new ToTest().toTest_unnamed_patterns__elide_type_patterns());
	}

	@Test(timeout = 200)
	@Points(exID = "UnnamedPatterns", bonus = 0.815, comment = "PublicTest \"toTest_unnamed_patterns__elide_just_name\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest_unnamed_patterns__elide_just_name() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42, new ToTest().toTest_unnamed_patterns__elide_just_name());
	}

	@Test(timeout = 200)
	@Points(exID = "UnnamedPatterns", bonus = 0.815, comment = "PublicTest \"toTest_unnamed_patterns__switch_expressions\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest_unnamed_patterns__switch_expressions() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42, new ToTest().toTest_unnamed_patterns__switch_expressions());
	}

	@Test(timeout = 200)
	@Points(exID = "UnnamedPatterns", bonus = 0.815, comment = "PublicTest \"toTest_unnamed_patterns__switch_expressions_multiple_patterns\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest_unnamed_patterns__switch_expressions_multiple_patterns() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42, new ToTest().toTest_unnamed_patterns__switch_expressions_multiple_patterns());
	}
}
