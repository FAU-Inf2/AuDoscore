import org.junit.jupiter.api.Timeout;
import tester.annotations.*;
import java.util.concurrent.TimeUnit;

@Exercises({@Ex(exID = "multiple_timeout", points = 12.5)})
public class UnitTest {
	@Points(exID = "multiple_timeout", bonus = 47.11)
	@Timeout(value = 10001, unit = TimeUnit.MILLISECONDS)
	public void test() {
	}

	@Timeout(value = 10) // default: SECONDS
	@Points(exID = "multiple_timeout", bonus = 7.11)
	public void test2() {
	}

	@Points(exID = "multiple_timeout", bonus = 47)
	@Timeout(value = 5) // default: SECONDS
	public void test3() {
	}

	@Timeout(value = 5) // default: SECONDS
	@Points(exID = "multiple_timeout", bonus = 41)
	public void test4() {
	}

	@Points(exID = "multiple_timeout", bonus = 4)
	@Timeout(value = 30) // default: SECONDS
	public void test5() {
	}
}
