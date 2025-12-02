import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@tester.annotations.Ex(exID = "compare_interface_specified_cleanroom_methods_not_found", points = 2)})
@CompareInterface({"ToTest.foo", "ToTest.bar"})
public class UnitTest {
	@Points(exID = "compare_interface_specified_cleanroom_methods_not_found", bonus = 1)
	public void test() {
		assertEquals(42, ToTest.toTest(), "Should return 42");
	}

	@Points(exID = "compare_interface_specified_cleanroom_methods_not_found", malus = 1)
	public void test2() {
		assertEquals(42, ToTest.toTest2(), "Should return 42");
	}
}
