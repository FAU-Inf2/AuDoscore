import org.junit.*;
import tester.annotations.*;

import static org.junit.Assert.*;

@SecretClass
public class SecretTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 300)
	@Points(exID = "Test1", bonus = 2)
	@Replace("Foo.getBars")
	public void testSecret() {
		Z[] b = new Foo().getBars();
		assertEquals(1, b[0].get());
		assertEquals(2, b[1].get());
	}
}

