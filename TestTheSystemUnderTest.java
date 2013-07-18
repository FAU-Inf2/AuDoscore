import org.junit.*;
import static org.junit.Assert.*;

@Exercises({ @Ex(exID = "GA4.6a", points = 7.5), @Ex(exID = "GA4.6b", points = 3.0) })
public class TestTheSystemUnderTest {
	// instead of explicitly coding the following rules here,
	// your test class can also just extend the class JUnitWithPoints
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Bonus(exID = "GA4.6a", bonus = 47.11)
	public void testFooShouldReturn4711() { // OK
		SystemUnderTest sut = new SystemUnderTest();
		assertEquals("Foo ist kaputt.", 4711, sut.foo());
	}

	@Test
	@Bonus(exID = "GA4.6b", bonus = 47.11, comment = "Bar should return 0.815 here.")
	public void testBarShouldReturn0815() { // FAILS
		SystemUnderTest sut = new SystemUnderTest();
		assertEquals("Bar ist kaputt.", 0.815, sut.bar(), 1e-3);
	}

	@Test
	@Malus(exID = "GA4.6b", malus = 8, comment = "Check if GA4.6b respects Blatt-00...")
	public void testBazShouldBeNice() { // OK
		SystemUnderTest sut = new SystemUnderTest();
		assertEquals("RTFM - Blatt-00!", "I am nice.", sut.baz());
	}

	@Test
	@Malus(exID = "GA4.6a", malus = 42.0815, comment = "Check if GA4.6a respects Blatt-00.")
	public void testFooBarShouldBeNice() { // FAILS
		SystemUnderTest sut = new SystemUnderTest();
		assertEquals("see Blatt-00: Thou shalt not use packages.", "I am nice.", sut.foobar());
	}
}