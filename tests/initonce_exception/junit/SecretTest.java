import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@InitializeOnce("getValue")
	static int value;

	static int getValue() {
		return ToTest.toTest();
	}

	@Points(exID = "initonce_exception", bonus = 1.0)
	public void secTest() {
		assertEquals(42, value);
	}
}
