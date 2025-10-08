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
	public void testSecret_without_replace_fails() {
		Z[] b = new Foo().getBars();
		assertEquals(1, b[0].get());
		assertEquals(2, b[1].get());
	}

	@Test(timeout = 300)
	@Points(exID = "Test1", bonus = 2)
	@Replace("Foo.getBars")
	public void testSecret_with_replace_passes() {
		Z[] b = new Foo().getBars();
		assertEquals(1, b[0].get());
		assertEquals(2, b[1].get());
	}
}

