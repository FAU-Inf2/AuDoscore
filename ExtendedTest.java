import org.junit.*;
import static org.junit.Assert.*;

@Exercises({ @Ex(exID = "GA4.6a", points = 7.5), @Ex(exID = "GA4.6b", points = 3.0), @Ex(exID = "GA4.6c", points = 7.5) })
public class ExtendedTest extends TestTheSystemUnderTest {
	// super class TestTheSystemUnderTest should be made abstract,
	// such that JUnit will not even try to execute both...
	// ... or take otherwise care of that!

	@Test
	@Bonus(exID = "GA4.6c", bonus = 3)
	public void testFooRetested() {
		SystemUnderTest sut = new SystemUnderTest();
		assertEquals("Foo ist kaputt.", 4711, sut.foo());
	}
}