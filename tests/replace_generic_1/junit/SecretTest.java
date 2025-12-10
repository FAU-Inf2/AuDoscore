import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

import java.util.Collections;

@SecretClass
public class SecretTest {
	@Points(exID = "replace_generic_1", bonus = 1)
	@Replace({"ToTest.process"})
	public void testFoo() {
		final ToTest<Integer> toTest = new ToTest<>();
		assertEquals(Integer.valueOf(1), toTest.get(Collections.singletonList(1)));
	}
}
