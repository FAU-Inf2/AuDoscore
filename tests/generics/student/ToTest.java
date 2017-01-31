import java.util.List;

public class ToTest {
	public static <E extends Number> int test(E hermomix) {
		return hermomix.intValue();
	}

	public static <E extends Number> int test2(E[] est) {
		return est.length;
	}
}

