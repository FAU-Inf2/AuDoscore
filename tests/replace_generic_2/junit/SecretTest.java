import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

import java.util.Collections;

@SecretClass
public class SecretTest {
	@Points(exID = "replace_generic_2", bonus = 1)
	@Replace({"ToTest.get2"})
	public void testFoo() {
		final ToTest<Integer> toTest = new ToTest<>();
		assertEquals(Integer.valueOf(1), toTest.get(Collections.singletonList(1)));
	}
}
