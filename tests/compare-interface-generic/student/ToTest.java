import java.util.List;

public class ToTest<Foo> {

	public Foo field;

	public Foo getFirst(List<Foo> list) {
		return list.iterator().next();
	}

	public <X> X process(X Foo) {
		return Foo;
	}

	public void test(List<? extends Foo> list) { }
}

