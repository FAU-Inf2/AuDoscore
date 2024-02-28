public class ToTest {
	// ========== record (regular top level declaration) ==========
	public static int toTest_regular(int a, int b) {
		return getSum_regular(a, b);
	}

	private static int getSum_regular(int a, int b) {
		Point2D<String, Integer> p2D = new Point2D<>(a, b), p2Dx = new Point2D<>(a);
		assert p2Dx.a() == p2D.a();
		return p2D.sum();
	}

	// ========== record as (static) member of class ==========
	@SuppressWarnings("") // put here to test the PrettyPrinter during @Replace
	public record Point3D<K extends String, V>(int a, int b, int c) implements Comparable<K>, java.util.Comparator<V> {
		public static String s = "records may contain static fields, but never instance fields";

		// FIXME: This constructor will be removed by the PrettyPrinter during @Replace!
		/*
		public Point3D(int a, int b, int c) {
			this.a = a;
			this.b = b;
			this.c = a;
		}
		 */

		public Point3D(int x) {
			this(x, x, x);
		}

		public int sum() {
			return a + b;
		}

		@Override
		public int compareTo(K k) {
			return 0;
		}

		@Override
		public int compare(V v1, V v2) {
			return 0;
		}
	}


	public static int toTest_member(int a, int b, int c) {
		return getSum_member(a, b, c);
	}

	private static int getSum_member(int a, int b, int c) {
		Point3D<String, Integer> p3D = new Point3D<>(a, b, c), p3Dx = new Point3D<>(a + b + c + Point3D.s.length());
		assert p3Dx.sum() != a + b;
		return p3D.sum(); // @Replace should replace wrong student code "return p3Dx.sum();" with expected code "return p3D.sum();"
	}

	// ========== record as (static) member of anonymous inner class/object ==========
	public static int toTest_member_anonymous_inner(int a, int b, int c, int d) {
		return getSum_member_anonymous_inner(a, b, c, d);
	}

	private static int getSum_member_anonymous_inner(int a, int b, int c, int d) {
		return new Object() {
			record Point4D(int a, int b, int c, int d) {
				public int sum() {
					return a + b;
				}
			}

			@Override
			public String toString() {
				return new Point4D(1, 2, 3, 4).toString();
			}

			int getSum_member_anonymous_inner(int a, int b, int c, int d) {
				return new Point4D(a, b, c, d).sum();
			}
		}.getSum_member_anonymous_inner(a, b, c, d); // @Replace should replace wrong student code "(a, a, c, d)" with expected code "(a, b, c, d)"
	}
}
