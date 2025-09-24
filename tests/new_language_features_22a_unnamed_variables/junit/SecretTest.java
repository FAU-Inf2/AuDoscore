import static org.junit.Assert.*;
import org.junit.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 200)
	@Points(exID = "UnnamedVariables", bonus = 0.815, comment = "SecretTest \"toTest_unnamed_variables__var_declaration_in_block\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_unnamed_variables__var_declaration_in_block")
	public void secTest__toTest_unnamed_variables__var_declaration_in_block() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42, new ToTest().toTest_unnamed_variables__var_declaration_in_block());
	}

	@Test(timeout = 200)
	@Points(exID = "UnnamedVariables", bonus = 0.815, comment = "SecretTest \"toTest_unnamed_variables__try_with_resources\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_unnamed_variables__try_with_resources")
	public void secTest__toTest_unnamed_variables__try_with_resources() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42, new ToTest().toTest_unnamed_variables__try_with_resources());
	}

	@Test(timeout = 200)
	@Points(exID = "UnnamedVariables", bonus = 0.815, comment = "SecretTest \"toTest_unnamed_variables__basic_for_loop\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_unnamed_variables__basic_for_loop")
	public void secTest__toTest_unnamed_variables__basic_for_loop() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42, new ToTest().toTest_unnamed_variables__basic_for_loop());
	}

	@Test(timeout = 200)
	@Points(exID = "UnnamedVariables", bonus = 0.815, comment = "SecretTest \"toTest_unnamed_variables__enhanced_for_loop\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_unnamed_variables__enhanced_for_loop")
	public void secTest__toTest_unnamed_variables__enhanced_for_loop() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42, new ToTest().toTest_unnamed_variables__enhanced_for_loop());
	}

	@Test(timeout = 200)
	@Points(exID = "UnnamedVariables", bonus = 0.815, comment = "SecretTest \"toTest_unnamed_variables__exception_parameter_of_catch_block\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_unnamed_variables__exception_parameter_of_catch_block")
	public void secTest__toTest_unnamed_variables__exception_parameter_of_catch_block() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42, new ToTest().toTest_unnamed_variables__exception_parameter_of_catch_block());
	}

	@Test(timeout = 200)
	@Points(exID = "UnnamedVariables", bonus = 0.815, comment = "SecretTest \"toTest_unnamed_variables__formal_parameter_of_lambda_expression\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest_unnamed_variables__formal_parameter_of_lambda_expression")
	public void secTest__toTest_unnamed_variables__formal_parameter_of_lambda_expression() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42, new ToTest().toTest_unnamed_variables__formal_parameter_of_lambda_expression());
	}
}
