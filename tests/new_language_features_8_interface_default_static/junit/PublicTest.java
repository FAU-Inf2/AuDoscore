import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "new_language_features_8_interface_default_static", points = 47.11)})
public class PublicTest {
	@Points(exID = "new_language_features_8_interface_default_static", bonus = 0.815)
	public void pubTest() {
		assertEquals(42, new ToTest().toTest(), "Should return 42.");
	}
}
