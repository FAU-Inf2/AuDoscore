import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "system_exit", points = 12.5)})
public class UnitTest {
	@Points(exID = "system_exit", bonus = 8)
	public void test1() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}

	@Points(exID = "system_exit", bonus = 1.33)
	public void test2() {
		ToTest.exit();
	}

	@Points(exID = "system_exit", bonus = 4)
	public void test3() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}
}
