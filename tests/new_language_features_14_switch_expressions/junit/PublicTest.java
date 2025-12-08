import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "new_language_features_14_switch_expressions", points = 47.11)})
public class PublicTest {
	@Points(exID = "new_language_features_14_switch_expressions", bonus = 0.815, comment = "PublicTest: Should fail in \"vanilla\" because without @Replace.")
	public void pubTest() {
		assertEquals(0, ToTest.toTest("MONDAY"), "Should fail in \"vanilla\" because without @Replace.");
	}
}
