import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "GA4.6a", points = 12.5)})
public class UnitTest {
	@Points(exID = "GA4.6a", bonus = 47)
	public void test() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}

	@Points(exID = "GA4.6a", malus = 11)
	public void test2() {
		assertEquals(23, ToTest.toTest2(), "Should return 23");
	}
}
