import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@InitializeOnce("getPid")
	private long[] pid = new long[1];

	static long[] getPid() {
		return new long[]{ProcessHandle.current().pid()};
	}

	// One of the tests below *must* fail

	@Points(exID = "initonce_really_once_field_nonstatic", bonus = 1.0)
	public void secTest_1() {
		assertTrue(pid[0] == getPid()[0], "Either this or the other secTest must fail - but NOT both!");
	}

	@Points(exID = "initonce_really_once_field_nonstatic", bonus = 1.0)
	public void secTest_2() {
		assertTrue(pid[0] == getPid()[0], "Either this or the other secTest must fail - but NOT both!");
	}
}
