import java.util.List;
public class ToTest<Foo> {
	public Foo get(List<Foo> list) {
		return process(list.iterator().next());
	}

	public Foo process(Foo elem) {
		return null;
	}
}

