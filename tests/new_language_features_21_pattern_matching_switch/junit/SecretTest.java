import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	// ========== simple ==========
	@Points(exID = "new_language_features_21_pattern_matching_switch", bonus = 0.815, comment = "SecretTest \"simple_switch_expression\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_simple_switch_expression")
	public void secTest_simple_switch_expression() {
		assertEquals(42 + 666L, ToTest.toTest_simple_switch_expression(new SimpleRectangle(42, 666L)), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_21_pattern_matching_switch", bonus = 0.815, comment = "SecretTest \"simple_switch_statement\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_simple_switch_statement")
	public void secTest_simple_switch_statement() {
		assertEquals(42 + 666L, ToTest.toTest_simple_switch_statement(new SimpleRectangle(42, 666L)), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_21_pattern_matching_switch", bonus = 0.815, comment = "SecretTest \"switch_expression_with_when\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_switch_expression_with_when")
	public void secTest_switch_expression_with_when() {
		assertEquals(42, ToTest.toTest_switch_expression_with_when(new SimpleCircle(-42)), "Should not fail.");
		assertEquals(42 + 666L, ToTest.toTest_switch_expression_with_when(new SimpleRectangle(42, 666L)), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_21_pattern_matching_switch", bonus = 0.815, comment = "SecretTest \"enum_switch_expression\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_enum_switch_expression")
	public void secTest_enum_switch_expression() {
		assertEquals(1, ToTest.toTest_enum_switch_expression(SimpleEnum.ALPHA), "Should not fail.");
		assertEquals(2, ToTest.toTest_enum_switch_expression(SimpleEnum.BETA), "Should pass in \"replaced\" because with @Replace now.");
	}

	// ========== generic ==========
	@Points(exID = "new_language_features_21_pattern_matching_switch", bonus = 0.815, comment = "SecretTest \"generic_switch_expression_type\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_generic_switch_expression_type")
	public void secTest_generic_switch_expression_type() {
		assertEquals(42 + 666L, ToTest.toTest_generic_switch_expression_type(new GenericRecord<>(42, new C(666L))), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_21_pattern_matching_switch", bonus = 0.815, comment = "SecretTest \"generic_switch_expression_var\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_generic_switch_expression_var")
	public void secTest_generic_switch_expression_var() {
		assertEquals(42 + 666L, ToTest.toTest_generic_switch_expression_var(new GenericRecord<>(42, new C(666L))), "Should pass in \"replaced\" because with @Replace now.");
	}
}
