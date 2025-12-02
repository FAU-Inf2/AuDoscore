import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "replace_generic_static", bonus = 1)
	@Replace({"ToTest.test"})
	public void secTest_test_A() {
		assertEquals(1, ToTest.test(1));
	}

	@Points(exID = "replace_generic_static", bonus = 1)
	@Replace({"ToTest.test"})
	public void secTest_test_B() {
		assertEquals(1, ToTest.test2(new Integer[]{1}));
	}
}
