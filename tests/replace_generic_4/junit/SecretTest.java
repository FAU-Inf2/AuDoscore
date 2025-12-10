import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "replace_generic_4", bonus = 1)
	@Replace({"ToTest.test"})
	public void testFoo() {
		final ToTest<Integer> toTest = new ToTest<>(1);
		assertEquals(0, toTest.test(1));
	}
}
