import static org.junit.Assert.assertEquals;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import tester.annotations.Ex;
import tester.annotations.Exercises;
import tester.annotations.Points;

@Exercises({ @Ex(exID = "GA4.6a", points = 12.5)})
public class UnitTest extends JUnitWithPoints {
	@Test(timeout=200)
	@Points(exID = "GA4.6a", bonus = 47.11)
	public void test() {
		assertEquals("Should return 42", 42, ToTest.toTest());
	}
}
