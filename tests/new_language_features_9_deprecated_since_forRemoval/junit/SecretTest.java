import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "new_language_features_9_deprecated_since_forRemoval", bonus = 0.815, comment = "SecretTest: Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.getSome")
	public void secTest() {
		assertEquals(42, ToTest.toTest(), "Should pass in \"replaced\" because with @Replace now.");
	}
}
