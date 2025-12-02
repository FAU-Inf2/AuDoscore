import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "forbidden_in_test", points = 1.0)})
@Forbidden({"java.lang.Integer"})
public class UnitTest {
	@Points(exID = "forbidden_in_test", bonus = 1.0)
	public void test() {
		assertEquals(Integer.parseInt("42"), ToTest.test());
	}
}
