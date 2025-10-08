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
	public static final PointsSummary pointsSummary = new PointsSummary();

	public enum Foo {
		FOO, BAR
	}

	@Test(timeout=100)
	@Points(exID = "EnumInSecret", bonus = 1)
	public void testSecret() {
		assertEquals(0, ToTest.test());
	}

}

