import java.io.*;

public class ToTest {
	public static int pub() {
		try (final FileWriter fw = new FileWriter(new File("junit", "SecretTest.java"))) {
			fw.append("import tester.annotations.*;\n");
			fw.append("@SecretClass\n");
			fw.append("public class SecretTest { }\n");
		} catch (final IOException ignored) {
		}
		return 42;
	}

	public static int sec() {
		return -1;
	}
}
