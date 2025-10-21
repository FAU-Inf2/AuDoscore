import static org.junit.Assert.assertEquals;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import tester.annotations.Points;
import tester.annotations.Replace;
import tester.annotations.SecretClass;

@SecretClass
public class SecretTest {
	// instead of explicitly coding the following rules here,
	// your test class can also just extend the class JUnitWithPoints
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 200)
	@Points(exID = "clean_vars", bonus = 1)
	@Replace({"ToTest.toTest_public_static_var_A"})
	public void secTest_toTest_public_static_var_A() {
		assertEquals("Should return 42", 42, ToTest.toTest_public_static_var_A());
	}

	@Test(timeout = 200)
	@Points(exID = "clean_vars", bonus = 1)
	@Replace({"ToTest.toTest_public_nonstatic_var_B", "ToTest.toTest_protected_static_var_C"}) // intentionally both
	public void secTest_toTest_public_nonstatic_var_B() {
		assertEquals("Should return 42", 42, new ToTest().toTest_public_nonstatic_var_B());
	}

	@Test(timeout = 200)
	@Points(exID = "clean_vars", bonus = 1)
	@Replace({"ToTest.toTest_protected_static_var_C", "ToTest.toTest_public_nonstatic_var_B"}) // intentionally both
	public void secTest_toTest_protected_static_var_C() {
		assertEquals("Should return 42", 42, ToTest.toTest_protected_static_var_C());
	}

	@Test(timeout = 200)
	@Points(exID = "clean_vars", bonus = 1)
	@Replace({"ToTest.toTest_protected_static_var_D"})
	public void secTest_toTest_protected_static_var_D() {
		assertEquals("Should return 42", 42, new ToTest().toTest_protected_static_var_D());
	}
}
