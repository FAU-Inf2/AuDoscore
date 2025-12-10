import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "replace_with_static", bonus = 1)
	@Replace({"ToTest.foo"})
	public void secTest() {
		assertEquals(42, new ToTest().bar());
	}
}
