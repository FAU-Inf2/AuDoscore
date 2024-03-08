import static org.junit.Assert.*;
import org.junit.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "Record Patterns", points = 47.11)})
public class PublicTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	// ========== simple ==========
	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "PublicTest \"simple_switch_expression\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_simple_switch_expression() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42 + 666L, ToTest.toTest_simple_switch_expression(new SimpleRectangle(42, 666L)));
	}

	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "PublicTest \"simple_switch_statement\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_simple_switch_statement() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42 + 666L, ToTest.toTest_simple_switch_statement(new SimpleRectangle(42, 666L)));
	}

	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "PublicTest \"switch_expression_with_when\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_switch_expression_with_when() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42 + 666L, ToTest.toTest_switch_expression_with_when(new SimpleRectangle(42, 666L)));
	}

	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "PublicTest \"enum_switch_expression\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_enum_switch_expression() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 2, ToTest.toTest_enum_switch_expression(SimpleEnum.BETA));
	}

	// ========== generic ==========
	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "PublicTest \"generic_switch_expression_type\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_generic_switch_expression_type() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42 + 666L, ToTest.toTest_generic_switch_expression_type(new GenericRecord<>(42, new C(666L))));
	}

	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "PublicTest \"generic_switch_expression_var\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_generic_switch_expression_var() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42 + 666L, ToTest.toTest_generic_switch_expression_var(new GenericRecord<>(42, new C(666L))));
	}
}
