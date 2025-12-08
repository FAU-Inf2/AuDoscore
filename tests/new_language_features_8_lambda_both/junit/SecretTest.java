import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "new_language_features_8_lambda_both", bonus = 0.815, comment = "secTest_toTest_getSome: Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.getSome")
	public void secTest_toTest_getSome() {
		assertEquals(1 + 3, ToTest.toTest_getSome(), "Should pass in \"replaced\" because with @Replace now.");
		assertEquals(1 + 3, ToTest.toTest_getSome(), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_8_lambda_both", bonus = 0.815, comment = "secTest_toTest_getSome_int: Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.getSome")
	public void secTest_toTest_getSome_int() {
		assertEquals(2 + 4, new ToTest().toTest_getSome_int(666), "Should pass in \"replaced\" because with @Replace now.");
		assertEquals(2 + 4, new ToTest().toTest_getSome_int(666), "Should pass in \"replaced\" because with @Replace now.");
	}
}
