import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "bonus_malus_correct", points = 12.5)})
public class UnitTest {
	@Points(exID = "bonus_malus_correct", bonus = 47)
	public void test() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}

	@Points(exID = "bonus_malus_correct", malus = 11)
	public void test2() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}
}
