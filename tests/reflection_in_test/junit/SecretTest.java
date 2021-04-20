import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;

import tester.annotations.SecretClass;
import tester.annotations.Points;

@SecretClass
public class SecretTest {
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Timeout(value = 500, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "ReflectionInTest", bonus = 1)
	public void test() {
		Method[] methods = ToTest.class.getDeclaredMethods();
		assertEquals(1, methods.length, "Should return 1");
	}
}

