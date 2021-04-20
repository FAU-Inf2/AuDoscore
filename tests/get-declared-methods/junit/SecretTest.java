import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;

import tester.annotations.*;
import tester.AuDoscoreUtils;

@SecretClass
public class SecretTest {
	/* IGNORE_FOR_PTC */

	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public static final PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Timeout(value = 500, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "GetDeclaredMethods", bonus = 1)
	public void test() {
		assertEquals(0, AuDoscoreUtils.getExplicitlyDeclaredMethods(ToTest.class).length);
	}
}

