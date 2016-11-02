import java.util.concurrent.atomic.AtomicInteger;

public class ToTest {
	
	private static final AtomicInteger val = new AtomicInteger();

	private static class Inner extends Thread {
		public void run() {
			val.set(42);
		}
	}

	public static int test() {
		final Inner inner = new Inner();
		inner.start();
		try {
			inner.join();
		} catch (InterruptedException e) {}
		return val.get();
	}
}

