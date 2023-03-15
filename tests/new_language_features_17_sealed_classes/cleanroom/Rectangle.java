public sealed class Rectangle extends Shape permits Square { // test "sealed" in non-abstract!
	@Override
	public int getSome() {
		return 42; // @Replace should replace wrong student code "return 0;" with expected code "return 42;"
	}
}
