import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "forbidden_wildcard", points = 1)})
@Forbidden({"java.util"})
@NotForbidden( //
		value = {"java.util.**List"}, //
		type = Forbidden.Type.WILDCARD //
)
public class UnitTest {
	@Points(exID = "forbidden_wildcard", bonus = 1)
	public void test() {
		assertEquals(0, ToTest.emptyList().size(), "Should return 0");
	}
}
