import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

import tester.annotations.*;

@SecretClass
public class SecretTest extends JUnitWithPoints {

	@Points(exID = "test", bonus = 1)
	@Test
	@Timeout(value  =  100, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	public void b() {
		assertEquals(42, ToTest.test());
	}
}

