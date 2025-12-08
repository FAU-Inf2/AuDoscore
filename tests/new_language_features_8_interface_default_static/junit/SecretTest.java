import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "new_language_features_8_interface_default_static", bonus = 0.815)
	@Replace("ToTest.getSome_default") // TODO: @Replace gives "INTERNAL ERROR" if student does NOT also @Override this method!
	public void secTest() {
		assertEquals(42, new ToTest().toTest(), "Should return 42.");
	}
}
