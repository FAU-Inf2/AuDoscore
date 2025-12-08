import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "new_language_features_9_deprecated_since_forRemoval", points = 47.11)})
public class PublicTest {
	@Points(exID = "new_language_features_9_deprecated_since_forRemoval", bonus = 0.815, comment = "PublicTest: Should fail in \"vanilla\" because without @Replace.")
	public void pubTest() {
		assertEquals(42, ToTest.toTest(), "Should fail in \"vanilla\" because without @Replace.");
	}
}
