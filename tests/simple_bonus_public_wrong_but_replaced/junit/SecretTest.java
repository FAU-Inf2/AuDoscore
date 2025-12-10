import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "simple_bonus_public_wrong_but_replaced", bonus = 47.11)
	@Replace({"ToTest.toTest"})
	public void secTest() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}
}
