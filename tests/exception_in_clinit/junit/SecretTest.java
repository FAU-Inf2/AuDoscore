import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	private static final ToTest tt = new ToTest(null);

	@Points(exID = "exception_in_clinit", bonus = 1)
	public void test2() {
		assertNotNull(tt);
	}
}
