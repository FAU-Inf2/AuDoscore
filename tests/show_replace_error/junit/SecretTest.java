import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "show_replace_error", bonus = 47)
	@Replace({"ToTest.toTest"})
	public void secTest() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}
}
