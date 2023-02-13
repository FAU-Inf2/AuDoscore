import java.util.*;

public class ToTest<T extends Comparable> {
	T field;

	public ToTest(T field) {
		this.field = field;
	}

	int test(Comparable value) {
		return value.compareTo(field);
	}
}

