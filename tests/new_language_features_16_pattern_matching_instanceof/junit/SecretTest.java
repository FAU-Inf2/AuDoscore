import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "new_language_features_16_pattern_matching_instanceof", bonus = 0.815, comment = "SecretTest \"simple\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.getSome_simple")
	public void secTest_simple() {
		assertEquals(4, ToTest.toTest_simple("Test"), "Should pass in \"replaced\" because with @Replace now.");
		assertEquals(42, ToTest.toTest_simple(42), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_16_pattern_matching_instanceof", bonus = 0.815, comment = "SecretTest \"moreComplex\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.getSome_moreComplex")
	public void secTest_moreComplex() {
		assertEquals(42, ToTest.toTest_moreComplex(), "Should pass in \"replaced\" because with @Replace now.");
	}
}
