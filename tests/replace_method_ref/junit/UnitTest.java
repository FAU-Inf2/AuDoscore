import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "replace_method_ref", points = 2.0)})
public class UnitTest {
	@Points(exID = "replace_method_ref", bonus = 0.00001)
	public void pubTest_foo_exists() {
		new ToTest().foo();
	}

	@Points(exID = "replace_method_ref", bonus = 0.00001)
	public void pubTest_bar_exists() {
		new ToTest().bar(() -> 13);
	}

	@Points(exID = "replace_method_ref", bonus = 0.00001)
	public void pubTest_baz_exists() {
		final int i = new ToTest().baz();
		assertTrue(i > 0);
	}
}
