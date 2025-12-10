import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "replace_unknown_class", bonus = 47.11)
	@Replace({"ToTest.toTest"})
	public void test() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}

	@Points(exID = "replace_unknown_class", bonus = 23.00)
	@Replace({"UnkownClass.toTest2"})
	public void test2() {
		assertEquals(23, ToTest.toTest2(), "Should return 23");
	}
}
