import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "multiple_interfaces", points = 12.5)})
public class UnitTest {
	@Points(exID = "multiple_interfaces", bonus = 47)
	public void test() {
		assertEquals(42, new ToTest().toTest(), "Should return 42");
	}

	@Points(exID = "multiple_interfaces", malus = 11)
	public void test2() {
		assertTrue(new ToTest2().toTest2(), "Should return true");
	}
}
