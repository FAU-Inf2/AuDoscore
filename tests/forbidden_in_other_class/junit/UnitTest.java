import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "forbidden_in_other_class", points = 1.0)})
@Forbidden({"java.lang.Integer"})
public class UnitTest {
	@Points(exID = "forbidden_in_other_class", bonus = 1.0)
	public void test() {
		assertEquals(0, ToTest.test());
	}
}
