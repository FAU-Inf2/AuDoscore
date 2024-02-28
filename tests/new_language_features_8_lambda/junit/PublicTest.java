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
	@Points(exID = "Lambda", bonus = 0.815, comment = "PublicTest: Should fail in \"vanilla\" because without @Replace.")
	public void pubTest() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", PublicTest.expected(), ToTest.toTest());
	}

	protected static int expected() {
		List<Integer> ints = new ArrayList<>(ToTest.DIGITS);
		ints.removeIf(x -> x % 2 == 0);
		int sum = 0;
		for (int i : ints) {
			sum += i;
		}
		return sum;
	}
}
