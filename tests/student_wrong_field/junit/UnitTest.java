import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@tester.annotations.Ex(exID = "student_wrong_field", points = 12.5)})
@CompareInterface({"ToTest.myBoolean", "ToTest.myInt"})
public class UnitTest {
	@Points(exID = "student_wrong_field", bonus = 47)
	public void test() {
		assertEquals(42, ToTest.toTest(42, 1), "Should return 42");
	}

	@Points(exID = "student_wrong_field", malus = 11)
	public void test2() {
		assertEquals(42, ToTest.toTest(42, 1), "Should return 42");
	}
}
