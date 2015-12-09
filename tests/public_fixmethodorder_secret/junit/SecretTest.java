import static org.junit.Assert.assertEquals;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import tester.annotations.Points;
import tester.annotations.SecretClass;

@SecretClass
public class SecretTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 666)
	@Points(exID = "Weite Felder - createAndFill", bonus = 0.5)
	public void secret_a() {
		assertEquals(0, Student.add(1));
	}

	@Test(timeout = 666)
	@Points(exID = "Weite Felder - createAndFill", bonus = 6.5)
	public void secret_b() {
		assertEquals(0, Student.add(2));
	}

	@Test(timeout = 666)
	@Points(exID = "Weite Felder - createAndFill", malus = 1.4)
	public void secret_c() {
		assertEquals(0, Student.add(39));
	}

	@Test(timeout = 666)
	@Points(exID = "Weite Felder - createAndFill", bonus = 0.4)
	public void secret_d() {
		assertEquals(0, Student.add(-5));
	}

}
