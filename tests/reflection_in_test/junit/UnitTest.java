import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "reflection_in_test", points = 2)})
public class UnitTest {
	@Points(exID = "reflection_in_test", bonus = 1)
	public void test() {
		ToTest t = new ToTest();
		assertEquals(42, t.test(), "Should return 42");
	}
}
