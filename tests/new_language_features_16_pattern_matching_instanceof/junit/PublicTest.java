import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "new_language_features_16_pattern_matching_instanceof", points = 47.11)})
public class PublicTest {
	@Points(exID = "new_language_features_16_pattern_matching_instanceof", bonus = 0.815, comment = "PublicTest \"simple\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_simple() {
		assertEquals(4, ToTest.toTest_simple("Test"), "Should fail in \"vanilla\" because without @Replace.");
		assertEquals(42, ToTest.toTest_simple(42), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_16_pattern_matching_instanceof", bonus = 0.815, comment = "PublicTest \"moreComplex\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_moreComplex() {
		assertEquals(42, ToTest.toTest_moreComplex(), "Should fail in \"vanilla\" because without @Replace.");
	}
}
