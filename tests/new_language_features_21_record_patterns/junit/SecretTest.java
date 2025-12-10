import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	// ========== simple record ==========
	@Points(exID = "new_language_features_21_record_patterns", bonus = 0.815, comment = "SecretTest \"simple\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_simple")
	public void secTest_simple() {
		assertEquals(42 + 666L, ToTest.toTest_simple(new SimpleRecord(42, 666L)), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_21_record_patterns", bonus = 0.815, comment = "SecretTest \"simple_variable\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_simple_variable")
	public void secTest_simple_variable() {
		assertEquals(42 + 666L, ToTest.toTest_simple_variable(new SimpleRecord(42, 666L)), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_21_record_patterns", bonus = 0.815, comment = "SecretTest \"simple_unapply_type\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_simple_unapply_type")
	public void secTest_simple_unapply_type() {
		assertEquals(42 + 666L, ToTest.toTest_simple_unapply_type(new SimpleRecord(42, 666L)), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_21_record_patterns", bonus = 0.815, comment = "SecretTest \"simple_unapply_var\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_simple_unapply_var")
	public void secTest_simple_unapply_var() {
		assertEquals(42 + 666L, ToTest.toTest_simple_unapply_var(new SimpleRecord(42, 666L)), "Should pass in \"replaced\" because with @Replace now.");
	}

	// ========== generic record ==========
	@Points(exID = "new_language_features_21_record_patterns", bonus = 0.815, comment = "SecretTest \"generic\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_generic")
	public void secTest_generic() {
		assertEquals(42 + 42, ToTest.toTest_generic(new GenericRecord<>(42)), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_21_record_patterns", bonus = 0.815, comment = "SecretTest \"generic_unapply_type\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_generic_unapply_type")
	public void secTest_generic_unapply_type() {
		assertEquals(42 + 42, ToTest.toTest_generic_unapply_type(new GenericRecord<>(42)), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_21_record_patterns", bonus = 0.815, comment = "SecretTest \"generic_unapply_var\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_generic_unapply_var")
	public void secTest_generic_unapply_var() {
		assertEquals(42 + 42, ToTest.toTest_generic_unapply_var(new GenericRecord<>(42)), "Should pass in \"replaced\" because with @Replace now.");
	}

	// ========== nested record ==========
	@Points(exID = "new_language_features_21_record_patterns", bonus = 0.815, comment = "SecretTest \"nested_unapply_type\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_nested_unapply_type")
	public void secTest_nested_unapply_type() {
		assertEquals(42 + 666, ToTest.toTest_nested_unapply_type(new NestedRecord<>(42, new NestedRecord<>(666, null))), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_21_record_patterns", bonus = 0.815, comment = "SecretTest \"nested_unapply_var\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_nested_unapply_var")
	public void secTest_nested_unapply_var() {
		assertEquals(42 + 666, ToTest.toTest_nested_unapply_var(new NestedRecord<>(42, new NestedRecord<>(666, null))), "Should pass in \"replaced\" because with @Replace now.");
	}
}
