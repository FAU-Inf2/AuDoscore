import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "forbidden_fixed", points = 1)})
@Forbidden({"java.util"})
@NotForbidden( //
		value = {"java.util.List", "java.util.LinkedList", "java.util.Collection"}, //
		type = Forbidden.Type.FIXED)
public class UnitTest {
	@Points(exID = "forbidden_fixed", bonus = 1)
	public void test() {
		assertEquals(0, ToTest.emptyList().size(), "Should return 0");
	}
}
