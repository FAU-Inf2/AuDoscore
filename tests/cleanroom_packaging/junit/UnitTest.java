import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "cleanroom_packaging", points = 42)})
public class UnitTest {
	@Points(exID = "cleanroom_packaging", bonus = 1)
	public void testPublic() {
		Z[] b = new Foo().getBars();
		assertEquals(2, b.length);
	}
}
