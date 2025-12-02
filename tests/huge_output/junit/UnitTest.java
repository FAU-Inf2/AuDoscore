import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "huge_output", points = 3)})
public class UnitTest {
	private static String getExpected() {
		final StringBuilder resultBuilder = new StringBuilder();
		for (int i = 0; i < 0x1000; ++i) {
			resultBuilder.append((char) ('a' + i % 26));
		}
		return resultBuilder.toString();
	}

	@Points(exID = "huge_output", bonus = 1)
	public void testFoo() {
		assertEquals(getExpected(), ToTest.foo(), "foo() is wrong!");
	}

	@Points(exID = "huge_output", bonus = 1)
	public void testBar() {
		assertEquals("a", ToTest.bar(), "bar() is wrong!");
	}

	@Points(exID = "huge_output", bonus = 1)
	public void testBaz() {
		assertEquals(getExpected(), ToTest.baz(), "baz() is wrong!");
	}
}
