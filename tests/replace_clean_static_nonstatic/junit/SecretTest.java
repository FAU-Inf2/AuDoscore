import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "replace_clean_static_nonstatic", bonus = 1)
	@Replace({"ToTest.toTest_public_static_var_A"})
	public void secTest_toTest_public_static_var_A() {
		assertEquals(42, ToTest.toTest_public_static_var_A(), "Should return 42");
	}

	@Points(exID = "replace_clean_static_nonstatic", bonus = 1)
	@Replace({"ToTest.toTest_public_nonstatic_var_B", "ToTest.toTest_protected_static_var_C"}) // intentionally both
	public void secTest_toTest_public_nonstatic_var_B() {
		assertEquals(42, new ToTest().toTest_public_nonstatic_var_B(), "Should return 42");
	}

	@Points(exID = "replace_clean_static_nonstatic", bonus = 1)
	@Replace({"ToTest.toTest_protected_static_var_C", "ToTest.toTest_public_nonstatic_var_B"}) // intentionally both
	public void secTest_toTest_protected_static_var_C() {
		assertEquals(42, ToTest.toTest_protected_static_var_C(), "Should return 42");
	}

	@Points(exID = "replace_clean_static_nonstatic", bonus = 1)
	@Replace({"ToTest.toTest_protected_static_var_D"})
	public void secTest_toTest_protected_static_var_D() {
		assertEquals(42, new ToTest().toTest_protected_static_var_D(), "Should return 42");
	}
}
