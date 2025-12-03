import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "import_studentonly", bonus = 1)
	@Replace({"ToTest.third"})
	public void test() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}

	@Points(exID = "import_studentonly", bonus = 1)
	public void test2() {
		assertEquals(23, ToTest.toTest2(), "Should return 23");
	}

	@Points(exID = "import_studentonly", bonus = 1)
	@Replace({"ToTest.third"})
	public void test3() {
		assertEquals(3, ToTest.third(), "Should return 3");
	}
}
