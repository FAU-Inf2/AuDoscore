import org.junit.*;
import tester.*;
import org.junit.*;
import org.junit.rules.*;
import static org.junit.Assert.*;
import java.lang.reflect.*;
import java.lang.*;
import java.util.*;
import java.io.*;

@Exercises({ @Ex(exID = "GA4.6a", points = 12.5)})
@Forbidden({"java.util.", "java.math.BigInteger"})
@NotForbidden({"java.util.HashMap", "java.util.LinkedList"})
public class ExampleTestcase {
	// instead of explicitly coding the following rules here,
	// your test class can also just extend the class JUnitWithPoints
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout=100)
	@Bonus(exID = "GA4.6a", bonus = 47.11)
	@Replace({"Student.foo"})
	public void testFooShouldReturn4711() { // correct in both
		assertEquals("Foo ist kaputt.", 4711, 4711);
	}

	@Test(timeout=100)
	@Bonus(exID = "GA4.6a", bonus = 47.11)
	@Replace({"Student.foo"})
	@SecretCase
	public void testFooShouldReturn4711_2() { // correct in student
		assertEquals("Foo ist kaputt.", 4712, (new Student()).foo());
	}

	@Test(timeout=100)
	@Bonus(exID = "GA4.6a", bonus = 47.11)
	@Replace({"Student.foo"})
	@SecretCase
	public void testFooShouldReturn4711_3() { // correct in both
		assertEquals("Foo ist kaputt.", (new Student()).foo(), (new Student()).foo());
	}

	@Test(timeout=100)
	@Bonus(exID = "GA4.6a", bonus = 47.11)
	@Replace({"Student.foo"})
	@SecretCase
	public void testFooShouldReturn4711_4() { // wrong in both
		assertEquals("Foo ist kaputt.", 23, 42);
	}

	@Test(timeout=10002)
	@Bonus(exID = "GA4.6a", bonus = 4.11)
	@Replace({"Student.foo"})
	@SecretCase
	public void testFooShouldReturn4711_5() { // correct in clean
		assertEquals("Foo ist kaputt.", 4711, (new Student()).foo());
	}

	@Test(timeout=10200)
	@Bonus(exID = "GA4.6a", bonus = 4.11)
	@Replace({"Student.bar"})
	@SecretCase
	public void testFooShouldReturn4711_6() { // correct in clean
		assertEquals("Foo ist kaputt.", 4711, (new Student()).foo());
	}

	@Test(timeout=10002)
	@Bonus(exID = "GA4.6a", bonus = 4.11)
	@Replace({"Student.foo"})
	@SecretCase
	public void testFooShouldReturn4712() { // correct in clean
		assertEquals("Foo ist kaputt.", 4712, (new Student()).foo());
	}

}
