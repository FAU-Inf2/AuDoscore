import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "points_calc_test_1", points = 10.0)})
public class UnitTest {
	@Points(exID = "points_calc_test_1", bonus = 0.0000001)
	public void test() {
		assertEquals(1, ToTest.toTest(), "Should return 1");
	}

	@Points(exID = "points_calc_test_1", bonus = 0.0000001)
	public void test2() {
		assertEquals(2, ToTest.toTest2(), "Should return 2");
	}

	@Points(exID = "points_calc_test_1", bonus = 0.0000001)
	public void test3() {
		assertEquals(3, ToTest.toTest3(), "Should return 3");
	}

	@Points(exID = "points_calc_test_1", bonus = 0.0000001)
	public void test4() {
		assertEquals(4, ToTest.toTest4(), "Should return 4");
	}

	@Points(exID = "points_calc_test_1", bonus = 0.0000001)
	public void test5() {
		assertEquals(5, ToTest.toTest5(), "Should return 5");
	}

	@Points(exID = "points_calc_test_1", bonus = 0.0000001)
	public void test6() {
		assertEquals(6, ToTest.toTest6(), "Should return 6");
	}

	@Points(exID = "points_calc_test_1", bonus = 0.0000001)
	public void test7() {
		assertEquals(7, ToTest.toTest7(), "Should return 7");
	}

	@Points(exID = "points_calc_test_1", bonus = 0.0000001)
	public void test8() {
		assertEquals(8, ToTest.toTest8(), "Should return 8");
	}

	@Points(exID = "points_calc_test_1", bonus = 0.0000001)
	public void test9() {
		assertEquals(9, ToTest.toTest9(), "Should return 9");
	}

	@Points(exID = "points_calc_test_1", bonus = 0.0000001)
	public void test10() {
		assertEquals(10, ToTest.toTest10(), "Should return 10");
	}
}
