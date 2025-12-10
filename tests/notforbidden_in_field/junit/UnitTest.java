import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Forbidden({"java."})
@NotForbidden({"java.lang.Object", "java.lang.Integer"})
@Exercises({@Ex(exID = "notforbidden_in_field", points = 12.5)})
public class UnitTest {
	@Points(exID = "notforbidden_in_field", bonus = 47.11)
	public void test() {
		final ToTest t = new ToTest();
		assertEquals(0, t.test());
	}
}
