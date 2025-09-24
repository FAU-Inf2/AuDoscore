public class ToTest {
	private final int[] data = {1, 2, 3, 4, 5, 6, 7};

	private int getRandomInt() {
		System.out.println("Introduce some side effect :(...");
		return new java.util.Random().nextInt();
	}

	// ========== Table 8-1 Valid Unnamed Variable Declarations ==========
	// A local variable declaration statement in a block
	public int toTest_unnamed_variables__var_declaration_in_block() {
		int sum = 0;
		for (int d : data) {
			var _ = getRandomInt(); // <=====
			sum += d;
		}
		System.out.println(sum);
		return 21; // @Replace should replace wrong student code with expected code here
	}

	// A resource specification of a try-with-resources statement
	public int toTest_unnamed_variables__try_with_resources() {
		try (var _ = new java.util.concurrent.ForkJoinPool()) { // <=====
			System.out.println("nop");
		} catch (Exception ignored) {
		}
		return 21; // @Replace should replace wrong student code with expected code here
	}

	// The header of a basic for statement
	public int toTest_unnamed_variables__basic_for_loop() {
		int count = 0;
		for (int i = 0, _ = getRandomInt(); i < data.length; i++) { // <===== TODO: fails upon replacement (probably due to JavaPrettyPrinter?): becomes "for (int i = 0,  = getRandomInt(); ..." instead
			count++;
		}
		System.out.println(count);
		return 21; // @Replace should replace wrong student code with expected code here
	}

	// The header of an enhanced for loop
	public int toTest_unnamed_variables__enhanced_for_loop() {
		int count = 0;
		for (int _ : data) { // <=====
			count++;
		}
		System.out.println(count);
		return 21; // @Replace should replace wrong student code with expected code here
	}

	// An exception parameter of a catch block
	public int toTest_unnamed_variables__exception_parameter_of_catch_block() {
		int count = 0;
		for (int d : data) {
			try {
				int i = Integer.parseInt("1" + d);
				System.out.println(i + " is valid");
			} catch (NumberFormatException _) { // <=====
				// ignored
			}
			count++;
		}
		System.out.println(count);
		return 21; // @Replace should replace wrong student code with expected code here
	}

	// A formal parameter of a lambda expression
	public int toTest_unnamed_variables__formal_parameter_of_lambda_expression() {
		java.util.stream.Stream<String> s = java.util.Arrays.stream(data).boxed().map(_ -> "Foo"); // <===== TODO: fails upon replacement (probably due to JavaPrettyPrinter?): becomes "...boxed().map(()->"Foo");" instead
		System.out.println(s.count());
		return 21; // @Replace should replace wrong student code with expected code here
	}
}
