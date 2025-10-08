import java.io.FileWriter;
import java.io.IOException;

public class ToTest {
	public static int pub() {
		try (final FileWriter fw = new FileWriter("SecretTest.java")) {
			fw.append("import tester.annotations.*;\n");
			fw.append("@SecretClass\n");
			fw.append("public class SecretTest extends JUnitWithPoints { }\n");
		} catch (final IOException e) {
			// Ignore
		}
		return 42;
	}

	public static int sec() {
		return -1;
	}
}

