import static org.junit.Assert.*;
import org.junit.*;
import tester.annotations.*;
import java.util.*;

@Exercises({@Ex(exID = "Lambda", points = 47.11)})
public class PublicTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815, comment = "PublicTest \"normal\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", PublicTest.expected(), ToTest.toTest());
	}

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815, comment = "PublicTest \"regression\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__regression() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", PublicTest.expected(), ToTest.toTestRegression());
	}

	protected static int expected() {
		var vs = new ArrayList<>(ToTest.DIGITS);
		vs.removeIf((var x) -> x % 2 == 0);
		var sum = 0;
		for (var i : vs) {
			sum += i;
		}
		return sum;
	}
}
