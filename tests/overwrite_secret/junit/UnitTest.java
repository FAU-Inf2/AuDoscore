import static org.junit.Assert.assertEquals;

import org.junit.Test;

import tester.annotations.*;

@Exercises({ @Ex(exID = "OverwriteSecret", points = 1.0) })
public class UnitTest extends JUnitWithPoints {

	@Test(timeout = 500)
	@Points(exID = "OverwriteSecret", bonus = 1.0)
	public void publicTest() {
		assertEquals(42, ToTest.pub());
	}
}

