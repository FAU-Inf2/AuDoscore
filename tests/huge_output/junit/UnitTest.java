import static org.junit.Assert.assertEquals;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import tester.annotations.*;

@Exercises({ @Ex(exID = "HugeOutput", points = 3) })
public class UnitTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	private static String getExpected() {
		final StringBuilder resultBuilder = new StringBuilder();
		for (int i = 0; i < 0x1000; ++i) {
			resultBuilder.append((char) ('a' + i % 26));
		}
		return resultBuilder.toString();
	}

	@Test(timeout = 1000)
	@Points(exID = "HugeOutput", bonus = 1)
	public void testFoo() {
		assertEquals("foo() is wrong!", getExpected(), ToTest.foo());
	}

	@Test(timeout = 1000)
	@Points(exID = "HugeOutput", bonus = 1)
	public void testBar() {
		assertEquals("bar() is wrong!", "a", ToTest.bar());
	}

	@Test(timeout = 1000)
	@Points(exID = "HugeOutput", bonus = 1)
	public void testBaz() {
		assertEquals("baz() is wrong!", getExpected(), ToTest.baz());
	}
}

