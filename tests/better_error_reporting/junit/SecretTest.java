import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "better_error_reporting", bonus = 23.00)
	@Replace({"ToTest.toTest2"})
	public void test2() {
		assertEquals(23, ToTest.toTest2(), "Should return 23");
	}
}
