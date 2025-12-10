import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "new_language_features_22a_unnamed_variables/expected", points = 47.11)})
public class PublicTest {
	@Points(exID = "new_language_features_22a_unnamed_variables/expected", bonus = 0.815, comment = "PublicTest \"toTest_unnamed_variables__var_declaration_in_block\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest_unnamed_variables__var_declaration_in_block() {
		assertEquals(42, new ToTest().toTest_unnamed_variables__var_declaration_in_block(), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_22a_unnamed_variables/expected", bonus = 0.815, comment = "PublicTest \"toTest_unnamed_variables__try_with_resources\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest_unnamed_variables__try_with_resources() {
		assertEquals(42, new ToTest().toTest_unnamed_variables__try_with_resources(), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_22a_unnamed_variables/expected", bonus = 0.815, comment = "PublicTest \"toTest_unnamed_variables__basic_for_loop\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest_unnamed_variables__basic_for_loop() {
		assertEquals(42, new ToTest().toTest_unnamed_variables__basic_for_loop(), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_22a_unnamed_variables/expected", bonus = 0.815, comment = "PublicTest \"toTest_unnamed_variables__enhanced_for_loop\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest_unnamed_variables__enhanced_for_loop() {
		assertEquals(42, new ToTest().toTest_unnamed_variables__enhanced_for_loop(), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_22a_unnamed_variables/expected", bonus = 0.815, comment = "PublicTest \"toTest_unnamed_variables__exception_parameter_of_catch_block\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest_unnamed_variables__exception_parameter_of_catch_block() {
		assertEquals(42, new ToTest().toTest_unnamed_variables__exception_parameter_of_catch_block(), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_22a_unnamed_variables/expected", bonus = 0.815, comment = "PublicTest \"toTest_unnamed_variables__formal_parameter_of_lambda_expression\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest_unnamed_variables__formal_parameter_of_lambda_expression() {
		assertEquals(42, new ToTest().toTest_unnamed_variables__formal_parameter_of_lambda_expression(), "Should fail in \"vanilla\" because without @Replace.");
	}
}
