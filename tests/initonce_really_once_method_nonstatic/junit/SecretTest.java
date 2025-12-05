import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@InitializeOnce("getPid")
	static long pid;

	private long getPid() {
		return ProcessHandle.current().pid();
	}

	// One of the tests below *must* fail

	@Points(exID = "initonce_really_once_method_nonstatic", bonus = 1.0)
	public void secTest_1() {
		assertTrue(pid == getPid(), "Either this or the other secTest must fail - but NOT both!");
	}

	@Points(exID = "initonce_really_once_method_nonstatic", bonus = 1.0)
	public void secTest_2() {
		assertTrue(pid == getPid(), "Either this or the other secTest must fail - but NOT both!");
	}
}
