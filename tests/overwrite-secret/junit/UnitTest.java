import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import tester.annotations.*;

@Exercises({ @Ex(exID = "OverwriteSecret", points = 1.0) })
public class UnitTest extends JUnitWithPoints {

	@Test
	@Timeout(value  =  500, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "OverwriteSecret", bonus = 1.0)
	public void publicTest() {
		assertEquals(42, ToTest.pub());
	}
}

