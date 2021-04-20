import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import tester.annotations.*;
import org.junit.jupiter.api.extension.RegisterExtension;

@SecretClass
public class SecretTest {
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Timeout(value  =  500, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "Generics", bonus = 1)
	@Replace({ "ToTest.test" })
	public void test1() {
		assertEquals(1, ToTest.test(Integer.valueOf(1)));
	}

	@Test
	@Timeout(value  =  500, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "Generics", bonus = 1)
	@Replace({ "ToTest.test" })
	public void test2() {
		assertEquals(1, ToTest.test2(new Integer[] { Integer.valueOf(1) }));
	}
}

