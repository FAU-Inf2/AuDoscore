import static org.junit.Assert.assertEquals;

import org.junit.Test;

import tester.annotations.*;

@SecretClass
public class SecretTest extends JUnitWithPoints {

	@Test(timeout = 500)
	@Points(exID = "OverwriteSecret", bonus = 1.0)
	public void secretTest() {
		assertEquals(23, ToTest.sec());
	}
}

