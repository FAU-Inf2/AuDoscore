import java.util.List;

public class ToTest {
	public static <T extends Number> int test(T hermomix) {
		return hermomix.intValue();
	}

	public static <T extends Number> int test2(T[] est) {
		return est.length;
	}
}

