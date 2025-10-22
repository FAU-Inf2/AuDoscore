package tester.tools;

import org.junit.internal.*;
import org.junit.runner.*;

public class SingleMethodRunner {
	static void main(String... args) throws ClassNotFoundException {
		if (args.length != 2) {
			System.err.println("Usage: class method");
			return;
		}
		String clazz = args[0];
		String method = args[1];
		Request request = Request.method(Class.forName(clazz), method);
		JUnitCore juc = new JUnitCore();
		juc.addListener(new TextListener(new RealSystem()));
		juc.run(request);
		System.exit(0);
	}
}
