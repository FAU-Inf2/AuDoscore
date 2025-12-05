import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "negative_timeouts", points = 2.0)})
public class UnitTest {
	@Timeout(-5)
	@Points(exID = "negative_timeouts", bonus = 0.00001)
	public void test() {
		assertEquals(42, ToTest.test());
	}
}
