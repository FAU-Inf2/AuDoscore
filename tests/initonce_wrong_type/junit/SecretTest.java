import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@InitializeOnce("getValue")
	static String value;

	static int getValue() {
		return 2;
	}

	@Points(exID = "initonce_wrong_type", bonus = 1.0)
	public void secTest() {
		assertNotNull(value);
	}
}
