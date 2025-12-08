import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "new_language_features_10_var_local_11_var_lambda", bonus = 0.815, comment = "SecretTest \"normal\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest")
	public void secTest() {
		assertEquals(PublicTest.expected(), ToTest.toTest(), "Should pass in \"replaced\" because with @Replace now.");
		assertEquals(PublicTest.expected(), ToTest.toTest(), "Should pass in \"replaced\" because with @Replace now.");
	}
}
