import java.util.List;
public class ToTest<Foo> {
	public Foo get(List<Foo> list) {
		return get2(list);
	}

	public <Bar extends Foo> Bar get2(List<Bar> list) {
		return null;
	}
}

