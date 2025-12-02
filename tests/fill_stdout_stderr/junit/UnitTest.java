import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "fill_stdout_stderr", points = 12.5)})
public class UnitTest {
	@Points(exID = "fill_stdout_stderr", bonus = 4)
	public void stdout() {
		assertEquals(42, ToTest.toTest(true), "Flooding stdOut should give 42.");
	}

	@Points(exID = "fill_stdout_stderr", bonus = 7)
	public void stderr() {
		assertEquals(666, ToTest.toTest(false), "Flooding stdErr should give 666.");
	}
}
