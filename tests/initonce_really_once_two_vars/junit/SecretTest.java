import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@InitializeOnce("getPidStatic")
	static long pidStatic;

	@InitializeOnce("getPid")
	long pid;

	static long getPidStatic() {
		return ProcessHandle.current().pid();
	}

	long getPid() {
		return ProcessHandle.current().pid();
	}

	// Exactly ONE of the tests below *must* succeed:

	@Points(exID = "initonce_really_once_two_vars", bonus = 1.0)
	public void secTest_1() {
		assertTrue(pidStatic == getPidStatic(), "Exactly ONE of the secTest_* must succeed!");
		assertTrue(pid == getPid(), "Exactly ONE of the secTest_* must succeed!");
	}

	@Points(exID = "initonce_really_once_two_vars", bonus = 1.0)
	public void secTest_2() {
		assertTrue(pidStatic == getPidStatic(), "Exactly ONE of the secTest_* must succeed!");
		assertTrue(pid == getPid(), "Exactly ONE of the secTest_* must succeed!");
	}

	@Points(exID = "initonce_really_once_two_vars", bonus = 1.0)
	public void secTest_3() {
		assertTrue(pidStatic == getPidStatic(), "Exactly ONE of the secTest_* must succeed!");
		assertTrue(pid == getPid(), "Exactly ONE of the secTest_* must succeed!");
	}

	@Points(exID = "initonce_really_once_two_vars", bonus = 1.0)
	public void secTest_4() {
		assertTrue(pidStatic == getPidStatic(), "Exactly ONE of the secTest_* must succeed!");
		assertTrue(pid == getPid(), "Exactly ONE of the secTest_* must succeed!");
	}
}
