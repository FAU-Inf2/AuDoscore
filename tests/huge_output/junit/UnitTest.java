import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import tester.annotations.*;
import org.junit.jupiter.api.extension.RegisterExtension;

@Exercises({ @Ex(exID = "HugeOutput", points = 3) })
public class UnitTest {
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public final static PointsSummary pointsSummary = new PointsSummary();

	private static String getExpected() {
		final StringBuilder resultBuilder = new StringBuilder();
		for (int i = 0; i < 0x1000; ++i) {
			resultBuilder.append((char) ('a' + i % 26));
		}
		return resultBuilder.toString();
	}

	@Test
	@Timeout(value  =  1000, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "HugeOutput", bonus = 1)
	public void testFoo() {
		assertEquals(getExpected(), ToTest.foo(), "foo() is wrong!");
	}

	@Test
	@Timeout(value  =  1000, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "HugeOutput", bonus = 1)
	public void testBar() {
		assertEquals("a", ToTest.bar(), "bar() is wrong!");
	}

	@Test
	@Timeout(value  =  1000, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "HugeOutput", bonus = 1)
	public void testBaz() {
		assertEquals(getExpected(), ToTest.baz(), "baz() is wrong!");
	}
}

