import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "new_language_features_22b_unnamed_patterns", bonus = 0.815, comment = "SecretTest \"toTest_unnamed_patterns__elide_type_patterns\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_unnamed_patterns__elide_type_patterns_helper")
	public void secTest__toTest_unnamed_patterns__elide_type_patterns() {
		assertEquals(42, new ToTest().toTest_unnamed_patterns__elide_type_patterns(), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_22b_unnamed_patterns", bonus = 0.815, comment = "SecretTest \"toTest_unnamed_patterns__elide_just_name\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_unnamed_patterns__elide_just_name_helper")
	public void secTest__toTest_unnamed_patterns__elide_just_name() {
		assertEquals(42, new ToTest().toTest_unnamed_patterns__elide_just_name(), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_22b_unnamed_patterns", bonus = 0.815, comment = "SecretTest \"toTest_unnamed_patterns__switch_expressions\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_unnamed_patterns__switch_expressions_helper")
	public void secTest__toTest_unnamed_patterns__switch_expressions() {
		assertEquals(42, new ToTest().toTest_unnamed_patterns__switch_expressions(), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_22b_unnamed_patterns", bonus = 0.815, comment = "SecretTest \"toTest_unnamed_patterns__switch_expressions_multiple_patterns\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_unnamed_patterns__switch_expressions_multiple_patterns_helper")
	public void secTest__toTest_unnamed_patterns__switch_expressions_multiple_patterns() {
		assertEquals(42, new ToTest().toTest_unnamed_patterns__switch_expressions_multiple_patterns(), "Should pass in \"replaced\" because with @Replace now.");
	}
}
