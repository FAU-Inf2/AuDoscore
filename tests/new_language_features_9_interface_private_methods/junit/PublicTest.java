import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "new_language_features_9_interface_private_methods", points = 42)})
@CompareInterface({"IToTest", "ToTest"})
public class PublicTest {
	@Points(exID = "new_language_features_9_interface_private_methods", bonus = 1, comment = "PublicTest \"default method\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest() {
		assertEquals(42, new ToTest().toTest_default(), "Should fail in \"vanilla\" because without @Replace.");
	}
}
