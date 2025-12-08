import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;
import java.util.*;

@Exercises({@Ex(exID = "new_language_features_10_var_local_11_var_lambda", points = 47.11)})
@NotForbidden(value = {"java.io.PrintWriter.**"}, type = Forbidden.Type.WILDCARD)
public class PublicTest {
	@Points(exID = "new_language_features_10_var_local_11_var_lambda", bonus = 0.815, comment = "PublicTest \"normal\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest() {
		assertEquals(PublicTest.expected(), ToTest.toTest(), "Should fail in \"vanilla\" because without @Replace.");
	}

	protected static int expected() {
		var vs = new ArrayList<>(ToTest.DIGITS);
		vs.removeIf((var x) -> x % 2 == 0);
		var sum = 0;
		for (var i : vs) {
			sum += i;
		}
		return sum;
	}
}
