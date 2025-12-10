import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "simple_bonus_public_exception", points = 12.5)})
public class UnitTest {
	@Points(exID = "simple_bonus_public_exception", bonus = 47.11)
	public void test() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}
}
