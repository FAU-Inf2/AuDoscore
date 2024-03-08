import static org.junit.Assert.*;
import org.junit.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	// ========== simple ==========
	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "SecretTest \"simple_switch_expression\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_simple_switch_expression")
	public void secTest_simple_switch_expression() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42 + 666L, ToTest.toTest_simple_switch_expression(new SimpleRectangle(42, 666L)));
	}

	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "SecretTest \"simple_switch_statement\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_simple_switch_statement")
	public void secTest_simple_switch_statement() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42 + 666L, ToTest.toTest_simple_switch_statement(new SimpleRectangle(42, 666L)));
	}

	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "SecretTest \"switch_expression_with_when\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_switch_expression_with_when")
	public void secTest_switch_expression_with_when() {
		assertEquals("Should not fail.", 42, ToTest.toTest_switch_expression_with_when(new SimpleCircle(-42)));
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42 + 666L, ToTest.toTest_switch_expression_with_when(new SimpleRectangle(42, 666L)));
	}

	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "SecretTest \"enum_switch_expression\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_enum_switch_expression")
	public void secTest_enum_switch_expression() {
		assertEquals("Should not fail.", 1, ToTest.toTest_enum_switch_expression(SimpleEnum.ALPHA));
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 2, ToTest.toTest_enum_switch_expression(SimpleEnum.BETA));
	}

	// ========== generic ==========
	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "SecretTest \"generic_switch_expression_type\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_generic_switch_expression_type")
	public void secTest_generic_switch_expression_type() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42 + 666L, ToTest.toTest_generic_switch_expression_type(new GenericRecord<>(42, new C(666L))));
	}

	@Test(timeout = 666)
	@Points(exID = "Record Patterns", bonus = 0.815, comment = "SecretTest \"generic_switch_expression_var\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_generic_switch_expression_var")
	public void secTest_generic_switch_expression_var() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42 + 666L, ToTest.toTest_generic_switch_expression_var(new GenericRecord<>(42, new C(666L))));
	}
}
