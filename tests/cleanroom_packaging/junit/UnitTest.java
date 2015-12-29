import org.junit.*;
import tester.annotations.*;

import static org.junit.Assert.*;

@Exercises({ @Ex(exID = "Test1", points = 42) })
public class UnitTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 300)
	@Points(exID = "Test1", bonus = 1)
	public void testPublic() {
		Z[] b = new Foo().getBars();
		assertEquals(2, b.length);
	}
}

