import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "overwrite_secret", bonus = 1.0)
	public void secretTest() {
		assertEquals(23, ToTest.sec());
	}
}
