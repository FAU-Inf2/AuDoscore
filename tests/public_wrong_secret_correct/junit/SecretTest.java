import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "public_wrong_secret_correct", bonus = 23.00)
	public void test2() {
		assertEquals(23, ToTest.toTest2(), "Should return 23");
	}
}
