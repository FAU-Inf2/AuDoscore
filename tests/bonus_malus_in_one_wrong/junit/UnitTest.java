import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "bonus_malus_in_one_wrong", points = 12.5)})
public class UnitTest {
	@Points(exID = "bonus_malus_in_one_wrong", bonus = 47, malus = 11)
	public void test() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}

	@Points(exID = "bonus_malus_in_one_wrong", bonus = 47, malus = 11)
	public void test2() {
		assertEquals(23, ToTest.toTest2(), "Should return 23");
	}
}
