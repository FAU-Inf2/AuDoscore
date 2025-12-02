import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "forbidden_in_array", points = 1.0)})
@Forbidden({"java.util."})
public class UnitTest {
	@Points(exID = "forbidden_in_array", bonus = 1)
	public void test() {
		assertEquals(0, ToTest.test());
	}
}
