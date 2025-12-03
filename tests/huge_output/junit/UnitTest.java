import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "huge_output", points = 19)})
public class UnitTest {
	private final static String EMPTY = "", PREFIX1 = "a", PREFIX2 = "ab", PREFIX3 = "abc";
	private final static String NORMAL = generate(1000, false);
	private final static String LONGER = generate(1234, false);
	private final static String ALIEN = generate(1000, true);

	private static String generate(int length, boolean withAlienChars) {
		final StringBuilder resultBuilder = new StringBuilder();
		for (int i = 0; i < length; ++i) {
			resultBuilder.append((withAlienChars && i >= 200 && i <= length - 200) ? '0' : (char) ('a' + i % 26));
		}
		return resultBuilder.toString();
	}

	// ----------------------------------------
	@Points(exID = "huge_output", bonus = 1)
	public void test__EMPTY__toTest() {
		assertEquals(EMPTY, ToTest.toTest(), "empty vs. ToTest.toTest() failed!");
	}

	// ----------------------------------------
	@Points(exID = "huge_output", bonus = 1)
	public void test__EMPTY__PREFIX1() {
		assertEquals(EMPTY, PREFIX1, "EMPTY vs. PREFIX1 failed!");
	}

	@Points(exID = "huge_output", bonus = 1)
	public void test__PREFIX1__EMPTY() {
		assertEquals(PREFIX1, EMPTY, "PREFIX1 vs. EMPTY failed!");
	}

	@Points(exID = "huge_output", bonus = 1)
	public void test__EMPTY__PREFIX2() {
		assertEquals(EMPTY, PREFIX2, "EMPTY vs. PREFIX2 failed!");
	}

	@Points(exID = "huge_output", bonus = 1)
	public void test__PREFIX2__EMPTY() {
		assertEquals(PREFIX2, EMPTY, "PREFIX2 vs. EMPTY failed!");
	}

	@Points(exID = "huge_output", bonus = 1)
	public void test__EMPTY__PREFIX3() {
		assertEquals(EMPTY, PREFIX3, "EMPTY vs. PREFIX3 failed!");
	}

	@Points(exID = "huge_output", bonus = 1)
	public void test__PREFIX3__EMPTY() {
		assertEquals(PREFIX3, EMPTY, "PREFIX3 vs. EMPTY failed!");
	}

	// ----------------------------------------
	@Points(exID = "huge_output", bonus = 1)
	public void test_EMPTY_NORMAL() {
		assertEquals(EMPTY, NORMAL, "EMPTY vs. NORMAL failed!");
	}

	@Points(exID = "huge_output", bonus = 1)
	public void test_PREFIX1_NORMAL() {
		assertEquals(PREFIX1, NORMAL, "PREFIX1 vs. NORMAL failed!");
	}

	@Points(exID = "huge_output", bonus = 1)
	public void test_PREFIX2_NORMAL() {
		assertEquals(PREFIX2, NORMAL, "PREFIX2 vs. NORMAL failed!");
	}

	@Points(exID = "huge_output", bonus = 1)
	public void test_PREFIX3_NORMAL() {
		assertEquals(PREFIX3, NORMAL, "PREFIX3 vs. NORMAL failed!");
	}

	@Points(exID = "huge_output", bonus = 1)
	public void test_NORMAL_EMPTY() {
		assertEquals(NORMAL, EMPTY, "NORMAL vs. EMPTY failed!");
	}

	@Points(exID = "huge_output", bonus = 1)
	public void test_NORMAL_PREFIX1() {
		assertEquals(NORMAL, PREFIX1, "NORMAL vs. PREFIX1 failed!");
	}

	@Points(exID = "huge_output", bonus = 1)
	public void test_NORMAL_PREFIX2() {
		assertEquals(NORMAL, PREFIX2, "NORMAL vs. PREFIX2 failed!");
	}

	@Points(exID = "huge_output", bonus = 1)
	public void test_NORMAL_PREFIX3() {
		assertEquals(NORMAL, PREFIX3, "NORMAL vs. PREFIX3 failed!");
	}

	// ----------------------------------------
	@Points(exID = "huge_output", bonus = 1)
	public void test_NORMAL_LONGER() {
		assertEquals(NORMAL, LONGER, "NORMAL vs. LONGER failed!");
	}

	@Points(exID = "huge_output", bonus = 1)
	public void test_LONGER_NORMAL() {
		assertEquals(LONGER, NORMAL, "LONGER vs. NORMAL failed!");
	}

	// ----------------------------------------
	@Points(exID = "huge_output", bonus = 1)
	public void test_NORMAL_ALIEN() {
		assertEquals(NORMAL, ALIEN, "NORMAL vs. ALIEN failed!");
	}

	@Points(exID = "huge_output", bonus = 1)
	public void test_ALIEN_NORMAL() {
		assertEquals(ALIEN, NORMAL, "ALIEN vs. NORMAL failed!");
	}
}
