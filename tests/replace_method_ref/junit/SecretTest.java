import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "replace_method_ref", bonus = 0.5)
	@Replace({"ToTest.baz"})
	public void secTest_foo() {
		assertEquals(42, new ToTest().foo(), "foo() is wrong");
	}

	@Points(exID = "replace_method_ref", bonus = 0.25)
	public void secTest_bar() {
		assertEquals(13, new ToTest().bar(() -> 13), "bar() is wrong");
	}

	@Points(exID = "replace_method_ref", bonus = 0.25)
	public void secTest_baz() {
		assertEquals(42, new ToTest().baz(), "baz() is wrong");
	}
}
