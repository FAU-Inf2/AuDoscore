import static org.junit.Assert.*;
import org.junit.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "UnnamedVariables", points = 47.11)})
public class PublicTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 200)
	@Points(exID = "UnnamedVariables", bonus = 0.815, comment = "PublicTest \"toTest_unnamed_variables__var_declaration_in_block\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest_unnamed_variables__var_declaration_in_block() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42, new ToTest().toTest_unnamed_variables__var_declaration_in_block());
	}

	@Test(timeout = 200)
	@Points(exID = "UnnamedVariables", bonus = 0.815, comment = "PublicTest \"toTest_unnamed_variables__try_with_resources\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest_unnamed_variables__try_with_resources() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42, new ToTest().toTest_unnamed_variables__try_with_resources());
	}

	@Test(timeout = 200)
	@Points(exID = "UnnamedVariables", bonus = 0.815, comment = "PublicTest \"toTest_unnamed_variables__basic_for_loop\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest_unnamed_variables__basic_for_loop() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42, new ToTest().toTest_unnamed_variables__basic_for_loop());
	}

	@Test(timeout = 200)
	@Points(exID = "UnnamedVariables", bonus = 0.815, comment = "PublicTest \"toTest_unnamed_variables__enhanced_for_loop\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest_unnamed_variables__enhanced_for_loop() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42, new ToTest().toTest_unnamed_variables__enhanced_for_loop());
	}

	@Test(timeout = 200)
	@Points(exID = "UnnamedVariables", bonus = 0.815, comment = "PublicTest \"toTest_unnamed_variables__exception_parameter_of_catch_block\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest_unnamed_variables__exception_parameter_of_catch_block() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42, new ToTest().toTest_unnamed_variables__exception_parameter_of_catch_block());
	}

	@Test(timeout = 200)
	@Points(exID = "UnnamedVariables", bonus = 0.815, comment = "PublicTest \"toTest_unnamed_variables__formal_parameter_of_lambda_expression\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest_unnamed_variables__formal_parameter_of_lambda_expression() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42, new ToTest().toTest_unnamed_variables__formal_parameter_of_lambda_expression());
	}
}
