import static org.junit.jupiter.api.Assertions.assertEquals;
import tester.annotations.*;

@SecretClass
class SecretTest {
	@Points(exID = "JUnit6", bonus = 1, comment = "SecretTest.")
	@Replace("ToTest.toTest")
	void secTest_toTest() {
		assertEquals(42, ToTest.toTest(), "SecTest failed!");
	}
}
