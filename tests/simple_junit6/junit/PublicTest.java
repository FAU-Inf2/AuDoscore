import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "JUnit6", points = 2)})
class PublicTest {
	@Points(exID = "JUnit6", bonus = 1, comment = "PublicTest.")
	void pubTest_toTest() {
		assertEquals(42, ToTest.toTest(), "PubTest failed!");
	}
}
