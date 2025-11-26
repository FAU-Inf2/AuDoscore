import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import tester.annotations.*;

@Exercises({@Ex(exID = "@BeforeAll_calls_student_code", points = 1)})
public class UnitTest {
	private static int result = 0;

	@BeforeAll
	public static void init() {
		result = ToTest.test();
	}

	@Points(exID = "@BeforeAll_calls_student_code", bonus = 1)
	public void test() {
		assertEquals(1, result);
	}
}

