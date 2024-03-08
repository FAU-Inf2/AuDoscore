import static org.junit.Assert.*;
import org.junit.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	// ========== simple record ==========
	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "SecretTest \"simple\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_simple")
	public void secTest_simple() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42 + 666L, ToTest.toTest_simple(new SimpleRecord(42, 666L)));
	}

	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "SecretTest \"simple_variable\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_simple_variable")
	public void secTest_simple_variable() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42 + 666L, ToTest.toTest_simple_variable(new SimpleRecord(42, 666L)));
	}

	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "SecretTest \"simple_unapply_type\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_simple_unapply_type")
	public void secTest_simple_unapply_type() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42 + 666L, ToTest.toTest_simple_unapply_type(new SimpleRecord(42, 666L)));
	}

	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "SecretTest \"simple_unapply_var\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_simple_unapply_var")
	public void secTest_simple_unapply_var() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42 + 666L, ToTest.toTest_simple_unapply_var(new SimpleRecord(42, 666L)));
	}

	// ========== generic record ==========
	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "SecretTest \"generic\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_generic")
	public void secTest_generic() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42 + 42, ToTest.toTest_generic(new GenericRecord<>(42)));
	}

	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "SecretTest \"generic_unapply_type\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_generic_unapply_type")
	public void secTest_generic_unapply_type() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42 + 42, ToTest.toTest_generic_unapply_type(new GenericRecord<>(42)));
	}

	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "SecretTest \"generic_unapply_var\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_generic_unapply_var")
	public void secTest_generic_unapply_var() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42 + 42, ToTest.toTest_generic_unapply_var(new GenericRecord<>(42)));
	}

	// ========== nested record ==========
	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "SecretTest \"nested_unapply_type\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_nested_unapply_type")
	public void secTest_nested_unapply_type() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42 + 666, ToTest.toTest_nested_unapply_type(new NestedRecord<>(42, new NestedRecord<>(666, null))));
	}

	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "SecretTest \"nested_unapply_var\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_nested_unapply_var")
	public void secTest_nested_unapply_var() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42 + 666, ToTest.toTest_nested_unapply_var(new NestedRecord<>(42, new NestedRecord<>(666, null))));
	}
}
