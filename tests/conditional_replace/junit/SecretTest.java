import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Replace(value = {"ToTest.replace"}, onlyIf = "field;ToTest.field;int[]")
	@Points(exID = "conditional_replace", bonus = 0.5)
	public void testSecret() {
		assertTrue(new ToTest(42).test());
	}
}
