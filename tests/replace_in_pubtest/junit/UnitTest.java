import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "replace_in_pubtest", points = 12.5)})
public class UnitTest {
	@Points(exID = "replace_in_pubtest", bonus = 47.11)
	@Replace({"ToTest.toTest"})
	public void test() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}

	@Points(exID = "replace_in_pubtest", bonus = 23.00)
	@Replace({"ToTest.toTest2"})
	public void test2() {
		assertEquals(23, ToTest.toTest2(), "Should return 23");
	}
}
