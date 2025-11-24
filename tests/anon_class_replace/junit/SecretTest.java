import static org.junit.jupiter.api.Assertions.assertEquals;

import tester.annotations.Points;
import tester.annotations.Replace;
import tester.annotations.SecretClass;

@SecretClass
public class SecretTest {
	@Points(exID = "AnonClassReplace", bonus = 1)
	@Replace({"ToTest.foo"})
	public void secTest() {
		assertEquals(42, ToTest.foo().next().intValue(), "Should return 42");
	}
}
