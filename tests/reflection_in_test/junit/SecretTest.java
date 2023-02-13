import static org.junit.Assert.assertEquals;

import java.lang.reflect.*;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import tester.annotations.SecretClass;
import tester.annotations.Points;

@SecretClass
public class SecretTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout=500)
	@Points(exID = "ReflectionInTest", bonus = 1)
	public void test() {
		Method[] methods = ToTest.class.getDeclaredMethods();
		assertEquals("Should return 1", 1, methods.length);
	}
}

