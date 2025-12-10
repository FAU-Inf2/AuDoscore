import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "plain_compile_error", points = 12.5)})
public class UnitTest {
	@Points(exID = "plain_compile_error", bonus = 47.11)
	public void test() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}
}
