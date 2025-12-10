import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "GA4.6a", points = 12.5)})
public class UnitTest {
	@org.junit.jupiter.api.Timeout(value = 60001, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GA4.6a", bonus = 47.11)
	public void test() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}
}
