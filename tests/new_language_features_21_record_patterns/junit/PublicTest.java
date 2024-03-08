import static org.junit.Assert.*;
import org.junit.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "Record Patterns", points = 47.11)})
public class PublicTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	// ========== simple record ==========
	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "PublicTest \"simple\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_simple() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42 + 42, ToTest.toTest_simple(new SimpleRecord(42)));
	}

	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "PublicTest \"simple_variable\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_simple_variable() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42 + 42, ToTest.toTest_simple_variable(new SimpleRecord(42)));
	}

	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "PublicTest \"simple_unapply_type\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_simple_unapply_type() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42 + 42, ToTest.toTest_simple_unapply_type(new SimpleRecord(42)));
	}

	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "PublicTest \"simple_unapply_var\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_simple_unapply_var() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42 + 42, ToTest.toTest_simple_unapply_var(new SimpleRecord(42)));
	}

	// ========== generic record ==========
	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "PublicTest \"generic\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_generic() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42 + 42, ToTest.toTest_generic(new GenericRecord<>(42)));
	}

	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "PublicTest \"generic_unapply_type\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_generic_unapply_type() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42 + 42, ToTest.toTest_generic_unapply_type(new GenericRecord<>(42)));
	}

	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "PublicTest \"generic_unapply_var\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_generic_unapply_var() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42 + 42, ToTest.toTest_generic_unapply_var(new GenericRecord<>(42)));
	}

	// ========== nested record ==========
	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "PublicTest \"nested_unapply_type\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_nested_unapply_type() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42 + 42, ToTest.toTest_nested_unapply_type(new NestedRecord<>(42, new NestedRecord<>(42, null))));
	}

	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "PublicTest \"nested_unapply_var\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_nested_unapply_var() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42 + 42, ToTest.toTest_nested_unapply_var(new NestedRecord<>(42, new NestedRecord<>(42, null))));
	}
}
