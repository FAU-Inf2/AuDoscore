import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@InitializeOnce("getPid")
	static int pid;

	// This is a really hackish way to get the process id of the JVM.
	// Source: https://stackoverflow.com/questions/35842/how-can-a-java-program-get-its-own-process-id
	static int getPid() {
		final String jvmName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
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

	@Points(exID = "initonce_really_once", bonus = 1.0)
	public void secTest_1() {
		assertEquals(pid, getPid());
	}

	@Points(exID = "initonce_really_once", bonus = 1.0)
	public void secTest_2() {
		assertEquals(pid, getPid());
	}
}
