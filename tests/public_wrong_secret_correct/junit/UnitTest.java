import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "public_wrong_secret_correct", points = 12.5)})
public class UnitTest {
	@Points(exID = "public_wrong_secret_correct", bonus = 47.11)
	public void test() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}
}
