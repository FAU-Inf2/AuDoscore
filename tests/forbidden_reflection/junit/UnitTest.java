import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "forbidden_reflection", points = 1)})
public class UnitTest {
	@Points(exID = "forbidden_reflection", bonus = 1)
	public void test() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}
}
