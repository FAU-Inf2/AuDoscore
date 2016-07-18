import static org.junit.Assert.*;

import org.junit.Test;
import tester.annotations.*;

@Exercises({ @Ex(exID = "ForbiddenInOtherClass", points = 1.0) })
@Forbidden({ "java.lang.Integer" })
public class UnitTest extends JUnitWithPoints {
	@Test(timeout = 500)
	@Points(exID = "ForbiddenInOtherClass", bonus = 1.0)
	public void test() {
		assertEquals(0, ToTest.test());
	}
}

