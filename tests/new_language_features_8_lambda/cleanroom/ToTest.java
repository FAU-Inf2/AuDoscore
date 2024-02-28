import java.util.*;

public class ToTest {
	public static final List<Integer> DIGITS = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

	public static int toTest() {
		List<Integer> some = getSome();
		int sum = 0;
		for (int i : some) {
			sum += i;
		}
		return sum;
	}

	private static List<Integer> getSome() {
		List<Integer> odds = new ArrayList<>(DIGITS);
		odds.removeIf(x -> x % 2 == 0); // @Replace should replace wrong student code "x -> x % 2 != 0" with expected code "x -> x % 2 == 0"
		return odds;
	}
}
