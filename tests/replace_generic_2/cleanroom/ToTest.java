import java.util.List;
public class ToTest<E> {
	public E get(List<E> list) {
		return get2(list);
	}

	public <T extends E> T get2(List<T> list) {
		final T E = list.iterator().next();
		return E;
	}
}

