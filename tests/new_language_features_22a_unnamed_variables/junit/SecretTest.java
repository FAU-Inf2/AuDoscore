import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "new_language_features_22a_unnamed_variables/expected", bonus = 0.815, comment = "SecretTest \"toTest_unnamed_variables__var_declaration_in_block\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_unnamed_variables__var_declaration_in_block")
	public void secTest__toTest_unnamed_variables__var_declaration_in_block() {
		assertEquals(42, new ToTest().toTest_unnamed_variables__var_declaration_in_block(), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_22a_unnamed_variables/expected", bonus = 0.815, comment = "SecretTest \"toTest_unnamed_variables__try_with_resources\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_unnamed_variables__try_with_resources")
	public void secTest__toTest_unnamed_variables__try_with_resources() {
		assertEquals(42, new ToTest().toTest_unnamed_variables__try_with_resources(), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_22a_unnamed_variables/expected", bonus = 0.815, comment = "SecretTest \"toTest_unnamed_variables__basic_for_loop\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_unnamed_variables__basic_for_loop")
	public void secTest__toTest_unnamed_variables__basic_for_loop() {
		assertEquals(42, new ToTest().toTest_unnamed_variables__basic_for_loop(), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_22a_unnamed_variables/expected", bonus = 0.815, comment = "SecretTest \"toTest_unnamed_variables__enhanced_for_loop\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_unnamed_variables__enhanced_for_loop")
	public void secTest__toTest_unnamed_variables__enhanced_for_loop() {
		assertEquals(42, new ToTest().toTest_unnamed_variables__enhanced_for_loop(), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_22a_unnamed_variables/expected", bonus = 0.815, comment = "SecretTest \"toTest_unnamed_variables__exception_parameter_of_catch_block\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_unnamed_variables__exception_parameter_of_catch_block")
	public void secTest__toTest_unnamed_variables__exception_parameter_of_catch_block() {
		assertEquals(42, new ToTest().toTest_unnamed_variables__exception_parameter_of_catch_block(), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_22a_unnamed_variables/expected", bonus = 0.815, comment = "SecretTest \"toTest_unnamed_variables__formal_parameter_of_lambda_expression\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_unnamed_variables__formal_parameter_of_lambda_expression")
	public void secTest__toTest_unnamed_variables__formal_parameter_of_lambda_expression() {
		assertEquals(42, new ToTest().toTest_unnamed_variables__formal_parameter_of_lambda_expression(), "Should pass in \"replaced\" because with @Replace now.");
	}
}
