import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "extends_jframe", bonus = 1)
	public void testSecret() {
		final ToTest toTest = new ToTest();
	}
}
