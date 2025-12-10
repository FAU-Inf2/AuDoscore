import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "replace_multiple_methods_with_regex", bonus = 1, comment = "SecretTest: Should pass in \"replaced\" because with @Replace now.")
	@Replace({"ToTest.toTest_.*"})
	public void secTest() {
		assertEquals(42 + 42, ToTest.toTest_alpha() + new ToTest().toTest_beta(), "Should pass in \"replaced\" because with @Replace now.");
	}
}
