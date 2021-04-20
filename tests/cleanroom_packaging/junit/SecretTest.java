import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import tester.annotations.*;

import static org.junit.jupiter.api.Assertions.*;

@SecretClass
public class SecretTest {
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Timeout(value = 300, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "Test1", bonus = 2)
	@Replace("Foo.getBars")
	public void testSecret() {
		Z[] b = new Foo().getBars();
		assertEquals(1, b[0].get());
		assertEquals(2, b[1].get());
	}
}

