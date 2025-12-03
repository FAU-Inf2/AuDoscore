import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "initonce_wrong_type", points = 1.0)})
public class UnitTest {
	@Points(exID = "initonce_wrong_type", bonus = 0.1)
	public void pubTest() {
		assertEquals(42, ToTest.toTest());
	}
}
