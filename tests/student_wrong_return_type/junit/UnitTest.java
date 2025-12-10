import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@tester.annotations.Ex(exID = "student_wrong_return_type", points = 12.5)})
@CompareInterface({"ToTest.toTest2"})
public class UnitTest {
	@Points(exID = "student_wrong_return_type", bonus = 47)
	public void test() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}

	@Points(exID = "student_wrong_return_type", malus = 11)
	public void test2() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}
}
