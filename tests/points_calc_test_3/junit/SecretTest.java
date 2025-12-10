import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "points_calc_test_3", bonus = 0.0000001)
	public void test11() {
		assertEquals(11, ToTest.toTest11(), "Should return 11");
	}

	@Points(exID = "points_calc_test_3", bonus = 0.0000001)
	public void test12() {
		assertEquals(12, ToTest.toTest12(), "Should return 12");
	}

	@Points(exID = "points_calc_test_3", malus = 0.0000001, bonus = 0.0000001)
	public void test13() {
		assertEquals(13, ToTest.toTest13(), "Should return 13");
	}

	@Points(exID = "points_calc_test_3", bonus = 0.0000001)
	public void test14() {
		assertEquals(14, ToTest.toTest14(), "Should return 14");
	}

	@Points(exID = "points_calc_test_3", bonus = 0.0000001)
	public void test15() {
		assertEquals(15, ToTest.toTest15(), "Should return 15");
	}

	@Points(exID = "points_calc_test_3", malus = 0.0000001, bonus = 0.0000001)
	public void test16() {
		assertEquals(16, ToTest.toTest16(), "Should return 16");
	}

	@Points(exID = "points_calc_test_3", bonus = 0.0000001)
	public void test17() {
		assertEquals(17, ToTest.toTest17(), "Should return 17");
	}

	@Points(exID = "points_calc_test_3", bonus = 0.0000001, malus = 0.0000001)
	public void test18() {
		assertEquals(18, ToTest.toTest18(), "Should return 18");
	}

	@Points(exID = "points_calc_test_3", bonus = 0.0000001)
	public void test19() {
		assertEquals(19, ToTest.toTest19(), "Should return 19");
	}

	@Points(exID = "points_calc_test_3", malus = 0.0000001, bonus = 0.0000001)
	public void test20() {
		assertEquals(20, ToTest.toTest20(), "Should return 20");
	}
}
