import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "public_correct_secret_wrong", points = 12.5)})
public class UnitTest {
	@Points(exID = "public_correct_secret_wrong", bonus = 47.11)
	public void test() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}
}
