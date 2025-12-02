import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "forbidden_prefix", points = 12.5)})
@Forbidden({"java.lang."})
@NotForbidden({"java.lang.String", "java.lang.Object"})
public class UnitTest {
	@Points(exID = "forbidden_prefix", bonus = 47.11)
	public void test() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}
}
