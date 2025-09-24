public class ToTestNestedOuter {
	public long counter = 4711;

	private void setCounter() {
		counter = 42; // @Replace should replace wrong student code with expected code here
	}

	public class ToTestNestedInner {
		public ToTestNestedInner() {
			setCounter();
			super();
		}
	}
}
