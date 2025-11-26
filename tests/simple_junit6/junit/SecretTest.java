import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
class SecretTest {
	@Points(exID = "JUnit6", bonus = 1, comment = "comment:secTest_toTest_alpha")
	@Replace("ToTest.toTest_alpha")
	void secTest_toTest_alpha() {
		assertEquals(42, ToTest.toTest_alpha(), "SecTest failed!");
	}

	@Points(exID = "JUnit6", bonus = 1)
	@Replace("ToTest.toTest_beta")
	void secTest_toTest_beta() {
		assertEquals("666", ToTest.toTest_beta(), "secTest_toTest_beta failed!");
	}
}
