import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "new_language_features_22b_unnamed_patterns", points = 47.11)})
public class PublicTest {
	@Points(exID = "new_language_features_22b_unnamed_patterns", bonus = 0.815, comment = "PublicTest \"toTest_unnamed_patterns__elide_type_patterns\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest_unnamed_patterns__elide_type_patterns() {
		assertEquals(42, new ToTest().toTest_unnamed_patterns__elide_type_patterns(), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_22b_unnamed_patterns", bonus = 0.815, comment = "PublicTest \"toTest_unnamed_patterns__elide_just_name\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest_unnamed_patterns__elide_just_name() {
		assertEquals(42, new ToTest().toTest_unnamed_patterns__elide_just_name(), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_22b_unnamed_patterns", bonus = 0.815, comment = "PublicTest \"toTest_unnamed_patterns__switch_expressions\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest_unnamed_patterns__switch_expressions() {
		assertEquals(42, new ToTest().toTest_unnamed_patterns__switch_expressions(), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_22b_unnamed_patterns", bonus = 0.815, comment = "PublicTest \"toTest_unnamed_patterns__switch_expressions_multiple_patterns\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest_unnamed_patterns__switch_expressions_multiple_patterns() {
		assertEquals(42, new ToTest().toTest_unnamed_patterns__switch_expressions_multiple_patterns(), "Should fail in \"vanilla\" because without @Replace.");
	}
}
