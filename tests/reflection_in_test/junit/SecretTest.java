import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

import java.lang.reflect.*;

@SecretClass
public class SecretTest {
	@Points(exID = "reflection_in_test", bonus = 1)
	public void test() {
		Method[] methods = ToTest.class.getDeclaredMethods();
		assertEquals(1, methods.length, "Should return 1");
	}
}
