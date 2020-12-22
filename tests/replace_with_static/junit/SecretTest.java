import static org.junit.Assert.assertEquals;
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
	@Points(exID = "ReplaceWithStatic", bonus = 1)
	@Replace({ "ToTest.foo" })
	public void test1() {
		assertEquals(42, new ToTest().bar());
	}
}

