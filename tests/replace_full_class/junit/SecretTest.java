import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "replace_full_class", bonus = 47.11)
	@Replace({"ToTest.toTest2"}) // replacing wrong method intentionally
	public void test() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}

	@Points(exID = "replace_full_class", bonus = 23.00)
	@Replace({"ToTest"})
	public void test2() {
		assertEquals(23 * 42, ToTest.toTest2() * ToTest.toTest(), "Should return 23*42");
	}
}
