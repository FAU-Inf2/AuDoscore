import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "overwrite_secret", points = 1.0)})
public class UnitTest {
	@Points(exID = "overwrite_secret", bonus = 1.0)
	public void publicTest() {
		assertEquals(42, ToTest.pub());
	}
}
