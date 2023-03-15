import java.util.*;

public class ToTest {
	public static final List<Integer> DIGITS = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

	public static int toTest() {
		// use of var in local variable declaration outside loop:
		var some = new ArrayList<>(DIGITS);
		// use of var (1x) in lambda outside loop:
		some.removeIf((var x) -> x % 2 == 0); // @Replace should replace wrong student code "x -> x % 2 != 0" with expected code "x -> x % 2 == 0"
		var sum = 0;
		// use of var in for loop head:
		for (var i = 1; i <= some.size(); i++) {
			// use of var in local variable declaration inside for loop body:
			var v = some.get(i - 1);
			assert 0 <= v && v <= 10;
			// use of var (2x) in lambda inside for loop body:
			var sumCheck = some.stream().reduce((var x, var y) -> x + y);
			assert sumCheck.orElse(0) >= 0;
		}
		// use of var in for-each loop head:
		for (var i : some) {
			sum += i;
		}
		// use of var (2x) in lambda outside loop:
		var sumCheck = some.stream().reduce((var x, var y) -> x + y);
		assert sum == sumCheck.orElse(0);
		// use of var in try-with-resources:
		try (var out1 = new java.io.PrintWriter(System.out); var out2 = new java.io.PrintWriter(System.out)) {
			out1.flush();
			out2.flush();
		}
		return sum;
	}

	public static int toTestRegression() {
		// ensure that the PrettyPrinter still outputs valid Java code:
		// --- all simple ---
		for (int a = 0; a < DIGITS.size(); a++) {
			var v = DIGITS.get(a);
			if (v < 0 || 10 < v) {
				throw new IllegalArgumentException();
			}
		}
		// --- no init ---
		int b = 0;
		for (; b < DIGITS.size(); b++) {
			var v = DIGITS.get(b);
			if (v < 0 || 10 < v) {
				throw new IllegalArgumentException();
			}
		}
		// --- no condition ---
		for (int c = 0; ; c++) {
			if (c >= DIGITS.size()) break;
			var v = DIGITS.get(c);
			if (v < 0 || 10 < v) {
				throw new IllegalArgumentException();
			}
		}
		// --- no increment ---
		for (int d = 0; d < DIGITS.size(); ) {
			var v = DIGITS.get(d);
			if (v < 0 || 10 < v) {
				throw new IllegalArgumentException();
			}
			d++;
		}
		// --- nothing in the head ---
		int e = 0;
		for (; ; ) {
			if (e >= DIGITS.size()) break;
			var v = DIGITS.get(e);
			if (v < 0 || 10 < v) {
				throw new IllegalArgumentException();
			}
			e++;
		}
		// --- no body ---
		for (int a = 0; a < DIGITS.size(); a++) ; // yes: there should be no loop body!
		// --- several init and increment, definition in condition ---
		for (int f = 0, g = -1, h = 1, v; f < DIGITS.size() && g-- < 0; f++, h--) {
			v = DIGITS.get(f);
			if (v < 0 || 10 < v) {
				throw new IllegalArgumentException();
			}
		}
		return toTest();
	}
}
