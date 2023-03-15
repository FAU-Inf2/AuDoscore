@SuppressWarnings("") // put here to test the PrettyPrinter during @Replace
public record Point2D<K extends String, V>(int a, int b) implements Comparable<K>, java.util.Comparator<V> {
	public static String s = "records may contain static fields, but never instance fields";

	public Point2D(int x) {
		this(x, x);
	}

	// FIXME: @Replace does NOT replace methods in records!?
	public int sum() {
		return a - b; // @Replace should replace wrong student code "a - b" with expected code "a + b"
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
