import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import tester.annotations.*;

@SecretClass
public class SecretTest extends JUnitWithPoints {

	@Test
	@Timeout(value  =  500, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "OverwriteSecret", bonus = 1.0)
	public void secretTest() {
		assertEquals(23, ToTest.sec());
	}
}

