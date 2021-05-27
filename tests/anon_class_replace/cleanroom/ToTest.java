import java.util.Iterator;

public class ToTest {

	public static Iterator<Integer> foo() {
		return new Iterator<Integer>() {
			class Dummy {
				public int VALUE = 42;
			}

			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public Integer next() {
				return new Dummy().VALUE;
			}
		};
	}
}

