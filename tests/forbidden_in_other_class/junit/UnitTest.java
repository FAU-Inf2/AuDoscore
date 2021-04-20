import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import tester.annotations.*;

@Exercises({ @Ex(exID = "ForbiddenInOtherClass", points = 1.0) })
@Forbidden({ "java.lang.Integer" })
public class UnitTest extends JUnitWithPoints {
	@Test
	@Timeout(value  =  500, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "ForbiddenInOtherClass", bonus = 1.0)
	public void test() {
		assertEquals(0, ToTest.test());
	}
}

