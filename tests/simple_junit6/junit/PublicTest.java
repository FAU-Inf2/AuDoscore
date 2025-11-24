import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

class HelperTests {
	@Points(exID = "JUnit6", bonus = 1, comment = "comment:pubTest_toTest_helper")
	void pubTest_toTest_helper() {
		assertEquals(42, ToTest.toTest_alpha(), "pubTest_toTest_helper failed!");
		assertEquals("666", ToTest.toTest_beta(), "pubTest_toTest_helper failed!");
	}
}

@Exercises({@Ex(exID = "JUnit6", points = 4)})
class PublicTest extends HelperTests {
	@Points(exID = "JUnit6", bonus = 1, comment = "comment:pubTest_toTest_alpha")
	void pubTest_toTest_alpha() {
		assertEquals(42, ToTest.toTest_alpha(), "pubTest_toTest_alpha failed!");
	}

	@org.junit.jupiter.api.Disabled
	@Points(exID = "JUnit6", bonus = 1)
	void pubTest_toTest_beta() {
		assertEquals("666", ToTest.toTest_beta(), "pubTest_toTest_beta failed!");
	}
}
