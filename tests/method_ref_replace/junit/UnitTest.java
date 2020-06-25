import static org.junit.Assert.assertEquals;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import tester.annotations.Ex;
import tester.annotations.Exercises;
import tester.annotations.Points;

@Exercises({ @Ex(exID = "TestMethodRef", points = 2.0) })
public class UnitTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public static final PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout=1000)
	@Points(exID = "TestMethodRef", bonus = 0.00001)
	public void testInterfaceFoo() {
		new ToTest().foo();
	}

	@Test(timeout=1000)
	@Points(exID = "TestMethodRef", bonus = 0.00001)
	public void testInterfaceBar() {
		new ToTest().bar(() -> 13);
	}

	@Test(timeout=1000)
	@Points(exID = "TestMethodRef", bonus = 0.00001)
	public void testInterfaceBaz() {
		final int i = new ToTest().baz();
	}
}

