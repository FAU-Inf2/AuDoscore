import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "replaced_clean_variable", bonus = 47.11)
	@Replace({"ToTest.toTest"})
	public void secTest() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}
}
