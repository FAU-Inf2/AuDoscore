import java.util.*;

public class ToTest {
	public static final List<Integer> DIGITS = Arrays.asList(1, 2, 3, 4, 5, 6);

	public static int toTest_getSome() {
		List<Integer> some = getSome();
		int sum = 0;
		for (int i : some) {
			sum += i;
		}
		return sum; // clean: 1+3 / stud: 2+3+4+5
	}

	public int toTest_getSome_int(int v) {
		List<Integer> some = getSome(v);
		int sum = 0;
		for (int i : some) {
			sum += i;
		}
		return sum; // clean: 2+4 / stud: 2+3+4+5
	}

	private static List<Integer> getSome() { // intentionally overloaded!
		List<Integer> odds = new ArrayList<>(DIGITS);
		odds.removeFirst(); // @Replace should replace wrong student code "removeFirst" with expected code "x -> x % 2 == 0"
		odds.removeLast(); // @Replace should replace wrong student code "removeLast" with expected code "x == 5"
		return odds;
	}

	private List<Integer> getSome(int v) { // intentionally overloaded!
		System.out.println(v);
		List<Integer> odds = new ArrayList<>(DIGITS);
		odds.removeFirst(); // @Replace should replace wrong student code "removeFirst" with expected code "x -> x % 2 != 0"
		odds.removeLast(); // @Replace should replace wrong student code "removeLast" with expected code "x == 6"
		return odds;
	}
}
