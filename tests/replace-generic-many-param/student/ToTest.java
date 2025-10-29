public class ToTest<Foo, Bar> {
	public ToTest(Foo foo, Bar bar) {
		System.out.println(foo.toString() + bar.toString());
	}

	public Foo getAlpha(Foo foo) {
		return null; // @Replace should replace wrong student code "return null" with expected code here
	}

	public Bar getBeta(Bar bar) {
		return null; // @Replace should replace wrong student code "return null" with expected code here
	}

	public static <Quux> Quux getGamma(Quux quux) {
		return null; // @Replace should replace wrong student code "return null" with expected code here
	}
}
