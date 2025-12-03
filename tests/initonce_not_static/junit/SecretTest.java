import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@InitializeOnce("getValue")
	static int value;

	int getValue() {
		return 1;
	}

	@Points(exID = "initonce_not_static", bonus = 1.0)
	public void secTest() {
		assertEquals(42, ToTest.toTest());
		assertEquals(value, getValue());
	}
}
