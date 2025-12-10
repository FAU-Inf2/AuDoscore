import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "replace_unknown_method", bonus = 1)
	@Replace({"ToTest.doesnotexist"})
	public void secTest_broken() {
	}
}
