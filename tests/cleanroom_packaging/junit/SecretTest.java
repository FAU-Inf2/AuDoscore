import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "cleanroom_packaging", bonus = 2)
	public void testSecret_without_replace_fails() {
		Z[] b = new Foo().getBars();
		assertEquals(1, b[0].get());
		assertEquals(2, b[1].get());
	}

	@Points(exID = "cleanroom_packaging", bonus = 2)
	@Replace("Foo.getBars")
	public void testSecret_with_replace_passes() {
		Z[] b = new Foo().getBars();
		assertEquals(1, b[0].get());
		assertEquals(2, b[1].get());
	}
}
