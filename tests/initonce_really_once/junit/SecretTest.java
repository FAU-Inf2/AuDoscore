import static org.junit.Assert.assertTrue;

import java.lang.management.ManagementFactory;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import tester.annotations.*;

@SecretClass
@SafeCallers({ "SecretTest" })
public class SecretTest {
	// instead of explicitly coding the following rules here,
	// your test class can also just extend the class JUnitWithPoints
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@InitializeOnce("getPid")
	static int pid;

	// This is a really hackish way to get the process id of the JVM.
	// Source: https://stackoverflow.com/questions/35842/how-can-a-java-program-get-its-own-process-id
	static int getPid() {
		final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
		final int index = jvmName.indexOf('@');

		if (index < 1) {
			// part before '@' empty (index = 0) / '@' not found (index = -1)
			return -1;
		}

		try {
			return (int) Long.parseLong(jvmName.substring(0, index));
		} catch (NumberFormatException e) {
			// ignore
		}
		return -1;
	}

	// One of the tests below *must* fail

	@Test(timeout=500)
	@Points(exID = "InitOnce_Really_Once", bonus = 1.0)
	public void testSecret1() {
		assertTrue(pid == getPid());
	}

	@Test(timeout=500)
	@Points(exID = "InitOnce_Really_Once", bonus = 1.0)
	public void testSecret2() {
		assertTrue(pid == getPid());
	}
}

