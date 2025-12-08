import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "new_language_features_9_interface_private_methods", bonus = 1, comment = "SecretTest \"default method\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("IToTest.toTest_default")
	public void secTest() {
		assertEquals(42, new ToTest().toTest_default(), "Should pass in \"replaced\" because with @Replace now.");
	}
}
