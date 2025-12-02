import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "forbidden_math_max_but_not_min", points = 1)})
@Forbidden({"java.lang.Math.max"})
public class UnitTest {
	@Points(exID = "forbidden_math_max_but_not_min", bonus = 1)
	public void test() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}
}
