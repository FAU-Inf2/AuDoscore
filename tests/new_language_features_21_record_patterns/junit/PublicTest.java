import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "new_language_features_21_record_patterns", points = 47.11)})
public class PublicTest {
	// ========== simple record ==========
	@Points(exID = "new_language_features_21_record_patterns", bonus = 0.815, comment = "PublicTest \"simple\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_simple() {
		assertEquals(42 + 42, ToTest.toTest_simple(new SimpleRecord(42)), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_21_record_patterns", bonus = 0.815, comment = "PublicTest \"simple_variable\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_simple_variable() {
		assertEquals(42 + 42, ToTest.toTest_simple_variable(new SimpleRecord(42)), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_21_record_patterns", bonus = 0.815, comment = "PublicTest \"simple_unapply_type\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_simple_unapply_type() {
		assertEquals(42 + 42, ToTest.toTest_simple_unapply_type(new SimpleRecord(42)), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_21_record_patterns", bonus = 0.815, comment = "PublicTest \"simple_unapply_var\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_simple_unapply_var() {
		assertEquals(42 + 42, ToTest.toTest_simple_unapply_var(new SimpleRecord(42)), "Should fail in \"vanilla\" because without @Replace.");
	}

	// ========== generic record ==========
	@Points(exID = "new_language_features_21_record_patterns", bonus = 0.815, comment = "PublicTest \"generic\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_generic() {
		assertEquals(42 + 42, ToTest.toTest_generic(new GenericRecord<>(42)), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_21_record_patterns", bonus = 0.815, comment = "PublicTest \"generic_unapply_type\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_generic_unapply_type() {
		assertEquals(42 + 42, ToTest.toTest_generic_unapply_type(new GenericRecord<>(42)), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_21_record_patterns", bonus = 0.815, comment = "PublicTest \"generic_unapply_var\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_generic_unapply_var() {
		assertEquals(42 + 42, ToTest.toTest_generic_unapply_var(new GenericRecord<>(42)), "Should fail in \"vanilla\" because without @Replace.");
	}

	// ========== nested record ==========
	@Points(exID = "new_language_features_21_record_patterns", bonus = 0.815, comment = "PublicTest \"nested_unapply_type\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_nested_unapply_type() {
		assertEquals(42 + 42, ToTest.toTest_nested_unapply_type(new NestedRecord<>(42, new NestedRecord<>(42, null))), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_21_record_patterns", bonus = 0.815, comment = "PublicTest \"nested_unapply_var\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_nested_unapply_var() {
		assertEquals(42 + 42, ToTest.toTest_nested_unapply_var(new NestedRecord<>(42, new NestedRecord<>(42, null))), "Should fail in \"vanilla\" because without @Replace.");
	}
}
