import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "simple_bonus_public_exception2", points = 12.5)})
public class UnitTest {
	@Points(exID = "simple_bonus_public_exception2", bonus = 47.11)
	public void test() {
		// intentionally calling ".intValue()" here!
		assertEquals(42, ToTest.toTest().intValue(), "Should return 42");
	}
}
