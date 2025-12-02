import static org.junit.Assert.assertNotNull;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import tester.annotations.Points;
import tester.annotations.Replace;
import tester.annotations.SecretClass;

@SecretClass
public class SecretTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public static final PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 666)
	@Points(exID = "ReplaceGeneric", bonus = 1, comment = "Should fail in \"vanilla\" and pass in \"replaced\".")
	@Replace({"ToTest.getAlpha"})
	public void secTest_getAlpha() {
		final ToTest<String, Integer> toTest = new ToTest<>("Foo", 4711);
		assertNotNull("Should fail in \"vanilla\" and pass in \"replaced\".", toTest.getAlpha("Foo"));
	}

	@Test(timeout = 666)
	@Points(exID = "ReplaceGeneric", bonus = 1, comment = "Should fail in \"vanilla\" and pass in \"replaced\".")
	@Replace({"ToTest.getBeta"})
	public void secTest_getBeta() {
		final ToTest<String, Integer> toTest = new ToTest<>("Foo", 4711);
		assertNotNull("Should fail in \"vanilla\" and pass in \"replaced\".", toTest.getBeta(4711));
	}

	@Test(timeout = 666)
	@Points(exID = "ReplaceGeneric", bonus = 1, comment = "Should fail in \"vanilla\" and pass in \"replaced\".")
	@Replace({"ToTest.getGamma"})
	public void secTest_getGamma() {
		assertNotNull("Should fail in \"vanilla\" and pass in \"replaced\".", ToTest.getGamma("Foo"));
	}
}

