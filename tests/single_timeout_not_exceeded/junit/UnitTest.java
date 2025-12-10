import tester.annotations.*;

@Exercises({@Ex(exID = "single_no_timeout", points = 12.5)})
public class UnitTest {
	@org.junit.jupiter.api.Timeout(value = 60000, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "single_no_timeout", bonus = 47.11)
	public void pubTest_no_op() {
	}
}
