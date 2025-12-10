import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "replace_multiple_methods_with_regex", points = 1)})
public class PublicTest {
	@Points(exID = "replace_multiple_methods_with_regex", bonus = 0.1, comment = "PublicTest: Should fail in \"vanilla\" because without @Replace.")
	public void pubTest() {
		assertEquals(42 + 42, ToTest.toTest_alpha() + new ToTest().toTest_beta(), "Should fail in \"vanilla\" because without @Replace.");
	}
}
