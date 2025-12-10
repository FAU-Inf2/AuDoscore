import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "thread", points = 2.0)})
public class UnitTest {
	@Points(exID = "thread", bonus = 0.00001)
	public void test() {
		assertEquals(42, ToTest.test());
	}
}
