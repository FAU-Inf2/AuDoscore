public class ToTest extends ToTestSuper {
	public int y;

	public ToTest(int x) {
		// prologue:
		x++;
		x = checkAndCorrect(x);
		this.y = 42; // @Replace should not "forget" this code...
		// super invocation:
		super(x);
		// epilogue:
		this.x++;
		System.out.println(this.x);
		super.x--;
	}

	public static int checkAndCorrect(int x) {
		if (x > 0) {
			x = 42; // @Replace should replace wrong student code with expected code here
		}
		return x;
	}

	public int toTest() {
		return x;
	}
}