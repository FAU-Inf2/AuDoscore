import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 500)
	@Points(exID = "Generics", bonus = 1)
	@Replace({ "ToTest.test" })
	public void test1() {
		assertEquals(1, ToTest.test(Integer.valueOf(1)));
	}

	@Test(timeout = 500)
	@Points(exID = "Generics", bonus = 1)
	@Replace({ "ToTest.test" })
	public void test2() {
		assertEquals(1, ToTest.test2(new Integer[] { Integer.valueOf(1) }));
	}
}

