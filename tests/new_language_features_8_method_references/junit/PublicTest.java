import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "new_language_features_8_method_references", points = 47.11)})
public class PublicTest {
	@Points(exID = "new_language_features_8_method_references", bonus = 0.815, comment = "PublicTest \"static\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__static() {
		assertEquals(42, ToTest.toTest_static(), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_8_method_references", bonus = 0.815, comment = "PublicTest \"non-static\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__nonstatic() {
		assertEquals(42, new ToTest().toTest(), "Should fail in \"vanilla\" because without @Replace.");
	}
}
