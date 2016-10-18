import java.util.Collection;
import java.util.LinkedList;
public class ToTest {
	public static <T> Collection<T> emptyList() {
		return new LinkedList<T>();
	}
}

