public record SimpleRecord(int a, long b) {
	public SimpleRecord(int x) {
		this(x, x);
	}
}
