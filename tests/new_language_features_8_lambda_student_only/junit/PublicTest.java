import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "new_language_features_8_lambda_student_only", points = 47.11)})
public class PublicTest {
	@Points(exID = "new_language_features_8_lambda_student_only", bonus = 0.815, comment = "PublicTest: Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_toTest_getSome() {
		assertEquals(2 + 3 + 4 + 5, ToTest.toTest_getSome(), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_8_lambda_student_only", bonus = 0.815, comment = "PublicTest: Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_toTest_getSome_int() {
		assertEquals(2 + 3 + 4 + 5, new ToTest().toTest_getSome_int(666), "Should fail in \"vanilla\" because without @Replace.");
	}
}
