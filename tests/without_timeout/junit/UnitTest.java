import tester.annotations.*;

@Exercises({@Ex(exID = "without_timeout", points = 12.5)})
public class UnitTest {
	@org.junit.jupiter.api.Timeout(value = 10001, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "without_timeout", bonus = 47.11)
	public void test() {
	}

	@org.junit.jupiter.api.Timeout(value = 10000, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "without_timeout", bonus = 7.11)
	public void test2() {
	}

	@org.junit.jupiter.api.Timeout(value = 5000, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "without_timeout", bonus = 47)
	public void test3() {
	}

	@org.junit.jupiter.api.Timeout(value = 4999, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "without_timeout", bonus = 41)
	public void test4() {
	}

	@org.junit.jupiter.api.Test // XXX: no timeout here, (timeout=30000)
	// @Points(exID = "without_timeout", bonus = 4)
	public void test5() {
	}
}
