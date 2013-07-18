import static org.junit.Assert.*;

import org.junit.*;

public class AnotherTest extends TestTheSystemUnderTest {
	@Test
	@Bonus(exID = "GA4.6c", bonus = 3)
	public void testFooRetested() {
		SystemUnderTest sut = new SystemUnderTest();
		assertEquals("Foo ist kaputt.", 4711, sut.foo());
	}
}