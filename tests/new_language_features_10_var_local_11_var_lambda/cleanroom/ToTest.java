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

	// ================================================================================
	// ensure that the PrettyPrinter still outputs valid Java code:
	public static int toTestRegression() {
		toTestRegression_for_loop_A_1_simple_for();
		toTestRegression_for_loop_A_2_simple_for_each();
		toTestRegression_for_loop_B_1_no_init();
		toTestRegression_for_loop_B_2_no_condition();
		toTestRegression_for_loop_B_3_no_increment();
		toTestRegression_for_loop_B_4_empty_head();
		toTestRegression_for_loop_B_5_multiple_init_and_def_in_condition_and_multiple_post_op();
		toTestRegression_for_loop_C_1_empty_body();
		toTestRegression_for_loop_D_1_nested_for_loop_body();
		toTestRegression_for_loop_D_2_nested_for_each_loop_body();
		toTestRegression_for_loop_D_3_nested_while_loop_body();
		toTestRegression_for_loop_D_4_nested_do_while_loop_body();
		toTestRegression_for_loop_D_5_labelled_nested_for_loop();
		toTestRegression_for_loop_E_01_single_expression_statement_body();
		toTestRegression_for_loop_E_02_method_call_body();
		toTestRegression_for_loop_E_03_switch_body();
		toTestRegression_for_loop_E_04_array_access_body();
		toTestRegression_for_loop_E_05_assert_body();
		toTestRegression_for_loop_E_06_assign_body();
		toTestRegression_for_loop_E_07_try_body();
		try {
			toTestRegression_for_loop_E_08_throw_body();
		} catch (IllegalArgumentException ignored) {
		}
		toTestRegression_for_loop_E_09_break();
		toTestRegression_for_loop_E_10_continue();
		toTestRegression_for_loop_E_11_return();
		toTestRegression_for_loop_E_12_field_access();
		toTestRegression_for_loop_E_13_synchronized();
		return toTest();
	}

	private static void toTestRegression_for_loop_A_1_simple_for() {
		for (int a = 0; a < DIGITS.size(); a++) { // intentionally not for-each!
			var v = DIGITS.get(0);
			if (v < 0 || 10 < v) {
				throw new IllegalArgumentException();
			}
		}
	}

	private static void toTestRegression_for_loop_A_2_simple_for_each() {
		for (int v : DIGITS) { // intentionally for-each!
			if (v < 0 || 10 < v) {
				throw new IllegalArgumentException();
			}
		}
	}

	private static void toTestRegression_for_loop_B_1_no_init() {
		int a = 0;
		for (; a < DIGITS.size(); a++) { // intentionally no init!
			var v = DIGITS.get(a);
			if (v < 0 || 10 < v) {
				throw new IllegalArgumentException();
			}
		}
	}

	private static void toTestRegression_for_loop_B_2_no_condition() {
		for (int a = 0; ; a++) { // intentionally no condition!
			if (a >= DIGITS.size()) break;
			var v = DIGITS.get(a);
			if (v < 0 || 10 < v) {
				throw new IllegalArgumentException();
			}
		}
	}

	private static void toTestRegression_for_loop_B_3_no_increment() {
		for (int a = 0; a < DIGITS.size(); ) { // intentionally no post-inc/dec!
			var v = DIGITS.get(a);
			if (v < 0 || 10 < v) {
				throw new IllegalArgumentException();
			}
			a++;
		}
	}

	private static void toTestRegression_for_loop_B_4_empty_head() {
		int a = 0;
		for (; ; ) { // intentionally no init, no condition, no post-inc/dec!
			if (a >= DIGITS.size()) break;
			var v = DIGITS.get(a);
			if (v < 0 || 10 < v) {
				throw new IllegalArgumentException();
			}
			a++;
		}
	}

	private static void toTestRegression_for_loop_B_5_multiple_init_and_def_in_condition_and_multiple_post_op() {
		for (int a = 0, b = -1, c = 1, v; a < DIGITS.size() && b-- < 0; a++, c--) { // intentionally quirky
			v = DIGITS.get(a);
			if (v < 0 || 10 < v) {
				throw new IllegalArgumentException();
			}
		}
	}

	private static void toTestRegression_for_loop_C_1_empty_body() {
		var x = 42;
		for (int a = 0; a < DIGITS.size(); a++) ; // intentionally no loop body!
		System.out.println(x);
	}

	private static void toTestRegression_for_loop_D_1_nested_for_loop_body() {
		for (int a = 0; a < DIGITS.size(); a++)
			for (int b = 0; b < DIGITS.size(); b++)
				System.out.println(a + "/" + b);
		System.out.println(DIGITS);
	}

	private static void toTestRegression_for_loop_D_2_nested_for_each_loop_body() {
		for (int a = 0; a < DIGITS.size(); a++)
			for (int b : DIGITS)
				System.out.println(a + "/" + b);
		System.out.println(DIGITS);
	}

	private static void toTestRegression_for_loop_D_3_nested_while_loop_body() {
		int a = 10;
		for (int b = 0; b < DIGITS.size(); b++)
			while (a-- > 0) System.out.println(b + "/" + a);
		System.out.println(DIGITS);
	}

	private static void toTestRegression_for_loop_D_4_nested_do_while_loop_body() {
		int a = 10;
		for (int b = 0; b < DIGITS.size(); b++)
			do System.out.println(b + "/" + a); while (a-- > 0);
		System.out.println(DIGITS);
	}

	private static void toTestRegression_for_loop_D_5_labelled_nested_for_loop() {
		for (int a = 0; a < DIGITS.size(); a++)
			middle:for (int b = 0; b < DIGITS.size(); b++)
				for (int c = 0; c < DIGITS.size(); c++)
					if (b == 5) break middle;
		System.out.println(DIGITS);
	}

	private static void toTestRegression_for_loop_E_01_single_expression_statement_body() {
		for (int a = 0; a < DIGITS.size(); a++)
			System.out.println(a);
		System.out.println(DIGITS);
	}

	private static void toTestRegression_for_loop_E_02_method_call_body() {
		for (int a = 0; a < DIGITS.size(); a++)
			toTest();
		System.out.println(DIGITS);
	}

	private static void toTestRegression_for_loop_E_03_switch_body() {
		for (int a = 0; a < DIGITS.size(); a++)
			switch (a) {
				case 0 -> System.out.println(0);
				case 1 -> System.out.println(1);
			}
		System.out.println(DIGITS);
	}

	private static void toTestRegression_for_loop_E_04_array_access_body() {
		int[] x = {0, 1, 2, 3, 4, 5};
		for (int a = 0; a < DIGITS.size(); a++)
			x[0]++;
		System.out.println(x[0]);
	}

	private static void toTestRegression_for_loop_E_05_assert_body() {
		for (int a = 0; a < DIGITS.size(); a++)
			assert DIGITS.get(0) >= 0;
		System.out.println(DIGITS);
	}

	private static void toTestRegression_for_loop_E_06_assign_body() {
		int y = 0;
		for (int a = 0; a < DIGITS.size(); a++)
			y = a;
		System.out.println(y);
	}

	private static void toTestRegression_for_loop_E_07_try_body() {
		int y = 0;
		for (int a = 0; a < DIGITS.size(); a++)
			try {
				y = Integer.parseInt("0");
			} catch (NumberFormatException ignored) {
			}
		System.out.println(y);
	}

	private static void toTestRegression_for_loop_E_08_throw_body() {
		for (int a = 0; a < DIGITS.size(); )
			throw new IllegalArgumentException(); // intentionally thrown
		System.out.println(DIGITS);
	}

	private static void toTestRegression_for_loop_E_09_break() {
		for (int a = 0; a < DIGITS.size(); )
			break; // intentionally break
		System.out.println(DIGITS);
	}

	private static void toTestRegression_for_loop_E_10_continue() {
		for (int a = 0; a < DIGITS.size(); a++)
			continue; // intentionally continue
		System.out.println(DIGITS);
	}

	private static void toTestRegression_for_loop_E_11_return() {
		for (int a = 0; a < DIGITS.size(); a++)
			return; // intentionally return
		System.out.println(DIGITS);
	}

	int aField = 0;

	private static void toTestRegression_for_loop_E_12_field_access() {
		ToTest tt = new ToTest();
		for (int a = 0; a < DIGITS.size(); a++)
			tt.aField++;
		System.out.println(DIGITS);
	}

	private static void toTestRegression_for_loop_E_13_synchronized() {
		for (int a = 0; a < DIGITS.size(); a++)
			synchronized (Integer.class) {
				System.out.println("String");
			}
		System.out.println(DIGITS);
	}
}
