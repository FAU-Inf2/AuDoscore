import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "locale", points = 1)})
public class UnitTest {
	@Points(exID = "locale", bonus = 1)
	public void test() {
		assertEquals(42, ToTest.test());
	}
}
