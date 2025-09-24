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
	@Points(exID = "UnnamedPatterns", bonus = 0.815, comment = "SecretTest \"toTest_unnamed_patterns__elide_type_patterns\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_unnamed_patterns__elide_type_patterns_helper")
	public void secTest__toTest_unnamed_patterns__elide_type_patterns() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42, new ToTest().toTest_unnamed_patterns__elide_type_patterns());
	}

	@Test(timeout = 200)
	@Points(exID = "UnnamedPatterns", bonus = 0.815, comment = "SecretTest \"toTest_unnamed_patterns__elide_just_name\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_unnamed_patterns__elide_just_name_helper")
	public void secTest__toTest_unnamed_patterns__elide_just_name() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42, new ToTest().toTest_unnamed_patterns__elide_just_name());
	}

	@Test(timeout = 200)
	@Points(exID = "UnnamedPatterns", bonus = 0.815, comment = "SecretTest \"toTest_unnamed_patterns__switch_expressions\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_unnamed_patterns__switch_expressions_helper")
	public void secTest__toTest_unnamed_patterns__switch_expressions() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42, new ToTest().toTest_unnamed_patterns__switch_expressions());
	}

	@Test(timeout = 200)
	@Points(exID = "UnnamedPatterns", bonus = 0.815, comment = "SecretTest \"toTest_unnamed_patterns__switch_expressions_multiple_patterns\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_unnamed_patterns__switch_expressions_multiple_patterns_helper")
	public void secTest__toTest_unnamed_patterns__switch_expressions_multiple_patterns() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42, new ToTest().toTest_unnamed_patterns__switch_expressions_multiple_patterns());
	}
}
