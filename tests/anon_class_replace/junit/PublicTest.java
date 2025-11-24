import tester.annotations.Ex;
import tester.annotations.Exercises;
import tester.annotations.Points;

@Exercises({@Ex(exID = "AnonClassReplace", points = 2)})
public class PublicTest {
	@Points(exID = "AnonClassReplace", bonus = 1)
	public void pubTest_no_op() {
	}
}
