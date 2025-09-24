public class ToTest {
	// ================================================================================
	// To simplify the code and improve readability, you can elide the type patterns ... with the unnamed pattern (_):
	public int toTest_unnamed_patterns__elide_type_patterns() {
		return toTest_unnamed_patterns__elide_type_patterns_helper(new Tuple3(666, "Foo", 0.815));
	}

	private int toTest_unnamed_patterns__elide_type_patterns_helper(Object o) {
		if (o instanceof Tuple3(Integer i, _, _)) {
			return 42; // @Replace should replace wrong student code with expected code here
		}
		return -1;
	}

	// ================================================================================
	// Alternatively, you can keep the type pattern's type and elide just its name:
	public int toTest_unnamed_patterns__elide_just_name() {
		return toTest_unnamed_patterns__elide_just_name_helper(new Tuple3(666, "Foo", 0.815));
	}

	private int toTest_unnamed_patterns__elide_just_name_helper(Object o) {
		if (o instanceof Tuple3(Integer i, String _, Double _)) {
			return 42; // @Replace should replace wrong student code with expected code here
		}
		return -1;
	}

	// ================================================================================
	// You can use unnamed patterns in switch expressions and statements:
	public int toTest_unnamed_patterns__switch_expressions() {
		return toTest_unnamed_patterns__switch_expressions_helper(new Tuple3(666, "Foo", 0.815));
	}

	private int toTest_unnamed_patterns__switch_expressions_helper(Object o) {
		return switch (o) {
			case Tuple2 t2 -> t2.i();
			case Tuple3 _ -> 42; // @Replace should replace wrong student code with expected code here
			default -> -1;
		};
	}

	// ================================================================================
	// You may use multiple patterns in a case label provided that they don't declare any pattern variables:
	public int toTest_unnamed_patterns__switch_expressions_multiple_patterns() {
		return toTest_unnamed_patterns__switch_expressions_multiple_patterns_helper(new Tuple3(666, "Foo", 0.815));
	}

	private int toTest_unnamed_patterns__switch_expressions_multiple_patterns_helper(Object o) {
		return switch (o) {
			case Tuple2 _, Tuple3 _ -> 42; // @Replace should replace wrong student code with expected code here
			default -> -1;
		};
	}
}
