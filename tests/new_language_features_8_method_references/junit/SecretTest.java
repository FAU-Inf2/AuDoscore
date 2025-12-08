import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "new_language_features_8_method_references", bonus = 0.815, comment = "SecretTest \"static\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.get42_static")
	public void secTest__static() {
		assertEquals(42, ToTest.toTest_static(), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_8_method_references", bonus = 0.815, comment = "SecretTest \"non-static\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.get42")
	public void secTest__nonstatic() {
		assertEquals(42, new ToTest().toTest(), "Should pass in \"replaced\" because with @Replace now.");
	}
}
