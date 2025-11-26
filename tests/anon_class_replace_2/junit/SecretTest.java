import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "anon_class_replace_2", bonus = 1)
	@Replace({"ToTest.get"})
	public void secTest() {
		assertEquals(0, ToTest.get(42, 42), "Should return 0");
	}
}
