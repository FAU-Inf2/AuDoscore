public class ToTest {
	// ========== simple ==========
	public static long toTest_simple_switch_expression(SimpleShape s) {
		return switch (s) {
			case SimpleCircle c -> c.radius() + 666;
			case SimpleRectangle r -> r.length() + r.width() + 666;
			default -> throw new IllegalArgumentException("Unrecognized shape");
		};
	}

	public static long toTest_simple_switch_statement(SimpleShape s) {
		// Switch statement can be replaced with enhanced 'switch' - but we want to test it this way!
		switch (s) {
			case SimpleCircle c:
				return c.radius() + 666;
			case SimpleRectangle r:
				return r.length() + r.width() + 666;
			default:
				throw new IllegalArgumentException("Unrecognized shape");
		}
	}

	public static long toTest_switch_expression_with_when(SimpleShape s) {
		return switch (s) {
			case SimpleCircle c when c.radius() <= 0 -> -c.radius() + 666;
			case SimpleCircle c -> c.radius() + 666;
			case SimpleRectangle r -> r.length() + r.width() + 666;
			default -> throw new IllegalArgumentException("Unrecognized shape");
		};
	}

	public static long toTest_enum_switch_expression(SimpleEnum s) {
		return switch (s) {
			case ALPHA -> 1 + 666;
			case SimpleEnum.BETA -> 2 + 666;
			case SimpleEnum e when e == SimpleEnum.GAMMA -> 3 + 666;
			case SimpleEnum e -> 4 + 666;
		};
	}

	// ========== generic ==========
	public static long toTest_generic_switch_expression_type(GenericRecord<Integer, A> g) {
		return switch (g) {
			case GenericRecord<Integer, A>(Integer t, B v) -> t + v.theInteger() + 666;
			case GenericRecord<Integer, A>(Integer t, C v) -> t + v.theLong() + 666;
		};
	}

	public static long toTest_generic_switch_expression_var(GenericRecord<Integer, A> g) {
		return switch (g) {
			case GenericRecord<Integer, A>(var t, B v) -> t + v.theInteger() + 666;
			case GenericRecord<Integer, A>(var t, C(var theLong)) -> t + theLong + 666;
		};
	}
}