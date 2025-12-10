import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Replace({"ToTest.test"})
	@Points(exID = "replace_with_fqn", bonus = 47.11)
	public void secTest() {
		assertEquals(1, new ToTest().test(java.util.List.of(42)));
	}
}
