import static org.junit.Assert.assertEquals;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import tester.annotations.*;
import tester.AuDoscoreUtils;

@SecretClass
public class SecretTest {
	/* IGNORE_FOR_PTC */

	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public static final PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout=500)
	@Points(exID = "GetDeclaredMethods", bonus = 1)
	public void test() {
		assertEquals(0, AuDoscoreUtils.getExplicitlyDeclaredMethods(ToTest.class).length);
	}
}

