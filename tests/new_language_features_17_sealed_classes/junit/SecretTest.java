import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "new_language_features_17_sealed_classes", bonus = 0.815, comment = "SecretTest: Should pass in \"replaced\" because with @Replace now.")
	@Replace({"Circle.getSome", "Rectangle.getSome"})
	public void secTest() {
		assertEquals(42 + 42, ToTest.toTest(), "Should pass in \"replaced\" because with @Replace now.");
	}
}
