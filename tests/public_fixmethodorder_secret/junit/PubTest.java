import static org.junit.Assert.*;
import org.junit.*;
import org.junit.rules.*;
import tester.*;
import tester.annotations.*;
import java.util.*;
import org.junit.runners.MethodSorters;

import org.junit.FixMethodOrder;

@Exercises({ @Ex(exID = "Weite Felder - createAndFill", points = 5) })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PubTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 666)
	@Bonus(exID = "Weite Felder - createAndFill", bonus = 0.5)
	public void public_a() {
		assertEquals(0, Student.add(1));
	}

	@Test(timeout = 666)
	@Bonus(exID = "Weite Felder - createAndFill", bonus = 1.5)
	public void public_b() {
		assertEquals(1, Student.add(2));
	}

	@Test(timeout = 666)
	@Bonus(exID = "Weite Felder - createAndFill", bonus = 9.4)
	public void public_c() {
		assertEquals(3, Student.add(39));
	}

	@Test(timeout = 666)
	@Bonus(exID = "Weite Felder - createAndFill", bonus = 0.4)
	public void public_d() {
		assertEquals(42, Student.add(-5));
	}

}
