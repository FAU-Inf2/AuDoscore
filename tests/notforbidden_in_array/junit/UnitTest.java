import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "notforbidden_in_array", points = 2.0)})
@Forbidden({"java.util."})
@NotForbidden({"java.util.Random"})
public class UnitTest {
	@Points(exID = "notforbidden_in_array", bonus = 1)
	public void test() {
		assertEquals(0, ToTest.test());
	}
}
