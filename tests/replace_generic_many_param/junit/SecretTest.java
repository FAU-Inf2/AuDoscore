import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "replace_generic_many_param", bonus = 1, comment = "Should fail in \"vanilla\" and pass in \"replaced\".")
	@Replace({"ToTest.getAlpha"})
	public void secTest_getAlpha() {
		final ToTest<String, Integer> toTest = new ToTest<>("Foo", 4711);
		assertNotNull(toTest.getAlpha("Foo"), "Should fail in \"vanilla\" and pass in \"replaced\".");
	}

	@Points(exID = "replace_generic_many_param", bonus = 1, comment = "Should fail in \"vanilla\" and pass in \"replaced\".")
	@Replace({"ToTest.getBeta"})
	public void secTest_getBeta() {
		final ToTest<String, Integer> toTest = new ToTest<>("Foo", 4711);
		assertNotNull(toTest.getBeta(4711), "Should fail in \"vanilla\" and pass in \"replaced\".");
	}

	@Points(exID = "replace_generic_many_param", bonus = 1, comment = "Should fail in \"vanilla\" and pass in \"replaced\".")
	@Replace({"ToTest.getGamma"})
	public void secTest_getGamma() {
		assertNotNull(ToTest.getGamma("Foo"), "Should fail in \"vanilla\" and pass in \"replaced\".");
	}
}
