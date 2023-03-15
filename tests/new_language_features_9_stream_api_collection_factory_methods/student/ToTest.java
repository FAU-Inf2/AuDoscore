import java.util.*;
import java.util.stream.*;

public class ToTest {
	public static int toTest__List_of__Stream_takeWhile() {
		List<Integer> l = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		// return l.parallelStream().takeWhile(x -> x <= 7).reduce(0, Integer::sum); // TODO: => java.security.AccessControlException: access denied ("java.lang.RuntimePermission" "enableContextClassLoaderOverride")
		return l.stream().takeWhile(x -> x <= 5).reduce(0, Integer::sum);
	}

	public static int toTest__List_of__Stream_dropWhile() {
		List<Integer> l = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		return l.stream().dropWhile(x -> x <= 5).reduce(0, Integer::sum);
	}

	public static int toTest__Set_of__Stream_filter() {
		Set<Integer> s = Set.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		return s.stream().filter(x -> x <= 5).reduce(0, Integer::sum);
	}

	public static int toTest__Map_of() {
		Map<Integer, Boolean> m = Map.of(0, false, 1, true, 2, false, 3, true, 4, false, 5, true, 6, false, 7, true, 8, false, 9, true);
		// no Stream from Map yet...
		int sum = 0;
		for (int k : m.keySet()) {
			sum += m.get(k) ? 5 : 0;
		}
		return sum;
	}

	public static int toTest__Stream_iterate_with_condition() {
		Stream<Integer> s = Stream.iterate(0, x -> x <= 5, x -> x + 1);
		return s.reduce(0, Integer::sum);
	}
}
