public class ToTest {
	// ========== simple record ==========
	public static long toTest_simple(Object o) { // classic instanceof (but with record)
		if (o instanceof SimpleRecord) {
			// Variable 'sr' can be replaced with pattern variable - but we want to test THIS syntax here!
			SimpleRecord sr = (SimpleRecord) o;
			return sr.a() + sr.b() + 666;
		} else {
			return 666;
		}
	}

	public static long toTest_simple_variable(Object o) { // type pattern
		if (o instanceof SimpleRecord p) {
			return p.a() + p.b() + 666;
		} else {
			return 666;
		}
	}

	public static long toTest_simple_unapply_type(Object o) { // new record pattern
		if (o instanceof SimpleRecord(int x, long y)) {
			return x + y + 666;
		} else {
			return 666;
		}
	}

	public static long toTest_simple_unapply_var(Object o) {
		if (o instanceof SimpleRecord(var x, var y)) {
			return x + y + 666;
		} else {
			return 666;
		}
	}

	// ========== generic record ==========
	public static long toTest_generic(Object o) {
		if (o instanceof GenericRecord<?>) { // ugly, but best Java can do now
			GenericRecord<Integer> sr = (GenericRecord<Integer>) o;
			return sr.t() + sr.t() + 666;
		} else {
			return 666;
		}
	}

	public static long toTest_generic_unapply_type(GenericRecord<?> o) {
		if (o instanceof GenericRecord(Integer t)) {
			return t + t + 666;
		} else {
			return 666;
		}
	}

	public static long toTest_generic_unapply_var(GenericRecord<Integer> o) {
		if (o instanceof GenericRecord(var t)) {
			return t + t + 666;
		} else {
			return 666;
		}
	}

	// ========== nested record ==========
	public static long toTest_nested_unapply_type(NestedRecord<Integer> o) {
		if (o instanceof NestedRecord(Integer h1, NestedRecord(Integer h2, NestedRecord<Integer> t))) {
			return h1 + h2 + 666;
		} else if (o instanceof NestedRecord(Integer h, NestedRecord<Integer> t)) {
			return h + 666;
		} else {
			return 666;
		}
	}

	public static long toTest_nested_unapply_var(NestedRecord<Integer> o) {
		if (o instanceof NestedRecord(var h1, NestedRecord(var h2, var t))) {
			return h1 + h2 + 666;
		} else if (o instanceof NestedRecord(var h, var t)) {
			return h + 666;
		} else {
			return 666;
		}
	}
}
