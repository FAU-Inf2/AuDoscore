import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import tester.annotations.*;

@Exercises({ @Ex(exID = "test", points = 47.11) })
public class UnitTest extends JUnitWithPoints {
	@Points(exID = "test", bonus = 1)
	@Test
	@Timeout(value = 100, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	public void a() {}
}

