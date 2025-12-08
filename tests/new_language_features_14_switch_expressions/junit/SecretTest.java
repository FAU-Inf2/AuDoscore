import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "new_language_features_14_switch_expressions", bonus = 0.815, comment = "SecretTest: Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest")
	public void secTest() {
		assertEquals(0, ToTest.toTest("MONDAY"), "Should pass in \"replaced\" because with @Replace now.");
	}
}
