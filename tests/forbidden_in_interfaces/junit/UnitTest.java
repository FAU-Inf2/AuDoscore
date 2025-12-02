import tester.annotations.*;

@Exercises({@Ex(exID = "forbidden_in_interfaces", points = 1)})
public class UnitTest {
	@Points(exID = "forbidden_in_interfaces", bonus = 1)
	public void test() {
		Super x = new ToTest();
		x.test();
	}
}
