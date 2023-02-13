import java.util.List;
public class ToTest<E> {
	public E get(List<E> list) {
		return process(list.iterator().next());
	}

	public E process(E elem) {
		return elem;
	}
}

