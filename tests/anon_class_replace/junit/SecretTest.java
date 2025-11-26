import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "anon_class_replace", bonus = 1)
	@Replace({"ToTest.foo"})
	public void secTest() {
		assertEquals(42, ToTest.foo().next().intValue(), "Should return 42");
	}
}
