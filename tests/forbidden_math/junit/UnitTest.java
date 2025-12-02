import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "forbidden_math", points = 1)})
@Forbidden({"java.lang.Math"})
public class UnitTest {
	@Points(exID = "forbidden_math", bonus = 1)
	public void test() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}
}
