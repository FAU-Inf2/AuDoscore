import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	private static class Foo implements ToTest {
	}

	@Points(exID = "replace_default_method", bonus = 0.5)
	@Replace({"ToTest.foo"})
	public void secTest_foo() {
		assertEquals(42, new Foo().foo(), "foo() is wrong");
	}
}
