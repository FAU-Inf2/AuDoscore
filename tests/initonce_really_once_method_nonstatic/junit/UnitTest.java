import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "initonce_really_once_method_nonstatic", points = 1.0)})
public class UnitTest {
	@Points(exID = "initonce_really_once_method_nonstatic", bonus = 0.1)
	public void pubTest() {
		assertEquals(42, ToTest.toTest());
	}
}
