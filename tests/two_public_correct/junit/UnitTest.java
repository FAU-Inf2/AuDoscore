import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "two_public_correct", points = 12.5)})
public class UnitTest {
	@Points(exID = "two_public_correct", bonus = 47.11)
	public void test() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}

	@Points(exID = "two_public_correct", bonus = 23.00)
	public void test2() {
		assertEquals(23, ToTest.toTest2(), "Should return 23");
	}
}
