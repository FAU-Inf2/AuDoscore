import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;

import tester.annotations.Points;
import tester.annotations.SecretClass;

@SecretClass
public class SecretTest {
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Timeout(value  =  666, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "Weite Felder - createAndFill", bonus = 0.5)
	public void secret_a() {
		assertEquals(0, Student.add(1));
	}

	@Test
	@Timeout(value  =  666, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "Weite Felder - createAndFill", bonus = 6.5)
	public void secret_b() {
		assertEquals(0, Student.add(2));
	}

	@Test
	@Timeout(value  =  666, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "Weite Felder - createAndFill", malus = 1.4)
	public void secret_c() {
		assertEquals(0, Student.add(39));
	}

	@Test
	@Timeout(value  =  666, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "Weite Felder - createAndFill", bonus = 0.4)
	public void secret_d() {
		assertEquals(0, Student.add(-5));
	}

}
