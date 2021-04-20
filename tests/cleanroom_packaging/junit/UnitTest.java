import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import tester.annotations.*;

import static org.junit.jupiter.api.Assertions.*;

@Exercises({ @Ex(exID = "Test1", points = 42) })
public class UnitTest {
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Timeout(value = 300, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "Test1", bonus = 1)
	public void testPublic() {
		Z[] b = new Foo().getBars();
		assertEquals(2, b.length);
	}
}

