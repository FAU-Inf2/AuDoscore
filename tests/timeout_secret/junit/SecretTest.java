import org.junit.Test;
import static org.junit.Assert.*;

import tester.annotations.*;

@SecretClass
public class SecretTest extends JUnitWithPoints {

	@Points(exID = "test", bonus = 1)
	@Test(timeout = 100)
	public void b() {
		assertEquals(42, ToTest.test());
	}
}

