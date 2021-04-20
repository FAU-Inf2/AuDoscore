import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;

import tester.annotations.Ex;
import tester.annotations.Exercises;
import tester.annotations.Points;

@Exercises({ @Ex(exID = "Weite Felder - createAndFill", points = 5) })
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class PubTest {
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Timeout(value  =  666, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "Weite Felder - createAndFill", bonus = 0.5)
	public void public_a() {
		assertEquals(0, Student.add(1));
	}

	@Test
	@Timeout(value  =  666, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "Weite Felder - createAndFill", bonus = 1.5)
	public void public_b() {
		assertEquals(1, Student.add(2));
	}

	@Test
	@Timeout(value  =  666, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "Weite Felder - createAndFill", bonus = 9.4)
	public void public_c() {
		assertEquals(3, Student.add(39));
	}

	@Test
	@Timeout(value  =  666, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "Weite Felder - createAndFill", bonus = 0.4)
	public void public_d() {
		assertEquals(42, Student.add(-5));
	}

}
