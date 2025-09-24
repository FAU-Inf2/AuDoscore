import module java.desktop; // declares and exports "java.awt.List"
import module java.base; // declares and exports "java.util.List"
import java.util.List; // resolve ambiguity by shadowing the above module imports

public class ToTest {
	public static int toTest() {
		List<Integer> DIGITS_LIST = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		System.out.println(DIGITS_LIST.size());
		Stream<Integer> DIGITS_STREAM = Stream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		System.out.println(DIGITS_STREAM.count());
		return 42; // @Replace should replace wrong student code with expected code here
	}
}
