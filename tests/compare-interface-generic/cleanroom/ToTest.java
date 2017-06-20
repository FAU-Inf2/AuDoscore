import java.util.List;

public class ToTest<E> {

	public E field;

	public E getFirst(List<E> list) {
		return list.iterator().next();
	}

	public <T> T process(T t) {
		return t;
	}

	public void test(List<? extends E> list) { }
}

