import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "new_language_features_21_pattern_matching_switch", points = 47.11)})
public class PublicTest {
	// ========== simple ==========
	@Points(exID = "new_language_features_21_pattern_matching_switch", bonus = 0.815, comment = "PublicTest \"simple_switch_expression\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_simple_switch_expression() {
		assertEquals(42 + 666L, ToTest.toTest_simple_switch_expression(new SimpleRectangle(42, 666L)), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_21_pattern_matching_switch", bonus = 0.815, comment = "PublicTest \"simple_switch_statement\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_simple_switch_statement() {
		assertEquals(42 + 666L, ToTest.toTest_simple_switch_statement(new SimpleRectangle(42, 666L)), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_21_pattern_matching_switch", bonus = 0.815, comment = "PublicTest \"switch_expression_with_when\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_switch_expression_with_when() {
		assertEquals(42 + 666L, ToTest.toTest_switch_expression_with_when(new SimpleRectangle(42, 666L)), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_21_pattern_matching_switch", bonus = 0.815, comment = "PublicTest \"enum_switch_expression\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_enum_switch_expression() {
		assertEquals(2, ToTest.toTest_enum_switch_expression(SimpleEnum.BETA), "Should fail in \"vanilla\" because without @Replace.");
	}

	// ========== generic ==========
	@Points(exID = "new_language_features_21_pattern_matching_switch", bonus = 0.815, comment = "PublicTest \"generic_switch_expression_type\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_generic_switch_expression_type() {
		assertEquals(42 + 666L, ToTest.toTest_generic_switch_expression_type(new GenericRecord<>(42, new C(666L))), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_21_pattern_matching_switch", bonus = 0.815, comment = "PublicTest \"generic_switch_expression_var\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_generic_switch_expression_var() {
		assertEquals(42 + 666L, ToTest.toTest_generic_switch_expression_var(new GenericRecord<>(42, new C(666L))), "Should fail in \"vanilla\" because without @Replace.");
	}
}
