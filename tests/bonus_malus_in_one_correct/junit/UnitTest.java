import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "bonus_malus_in_one_correct", points = 12.5)})
public class UnitTest {
	@Points(exID = "bonus_malus_in_one_correct", bonus = 47, malus = 11)
	public void test() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}

	@Points(exID = "bonus_malus_in_one_correct", bonus = 47, malus = 11)
	public void test2() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}
}
