public non-sealed class Circle extends Shape { // test "non-sealed"!
	@Override
	public int getSome() {
		return 42; // @Replace should replace wrong student code "return 0;" with expected code "return 42;"
	}
}
