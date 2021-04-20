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
	public static final PointsSummary pointsSummary = new PointsSummary();

	public enum Foo {
		FOO, BAR
	}

	@Test
	@Timeout(value = 100, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "EnumInSecret", bonus = 1)
	public void testSecret() {
		assertEquals(0, ToTest.test());
	}

}

