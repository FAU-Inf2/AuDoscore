import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "forbidden_math_notforbidden_max", points = 1)})
@Forbidden({"java.lang.Math"})
@NotForbidden({"java.lang.Math.max"})
public class UnitTest {
	@Points(exID = "forbidden_math_notforbidden_max", bonus = 1)
	public void test() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}
}
