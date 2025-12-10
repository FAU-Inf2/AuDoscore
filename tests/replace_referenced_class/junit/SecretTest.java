import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "replace_referenced_class", bonus = 47.11)
	@Replace({"ToReplace"})
	public void test() {
		assertEquals(42, new ToTest().get(), "Should return 42");
	}
}
