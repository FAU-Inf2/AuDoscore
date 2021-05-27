import java.util.Comparator;

public class ToTest {

	private static Comparator<Integer> __clean_comp = new Comparator<Integer>() {
		@Override
		public int compare(final Integer o1, final Integer o2) {
			return o1.compareTo(o2);
		}
	};

	public static int get(int a, int b) {
		return __clean_comp.compare(a, b);
	}
}

