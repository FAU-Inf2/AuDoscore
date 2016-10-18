import org.junit.Test;

import tester.annotations.*;

@Exercises({ @Ex(exID = "test", points = 47.11) })
public class UnitTest extends JUnitWithPoints {
	@Points(exID = "test", bonus = 1)
	@Test(timeout = 100)
	public void a() {}
}

