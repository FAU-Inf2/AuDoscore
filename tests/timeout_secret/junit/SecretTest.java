import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "timeout_secret", bonus = 1)
	public void secTest() {
		assertEquals(42, ToTest.test());
	}
}
