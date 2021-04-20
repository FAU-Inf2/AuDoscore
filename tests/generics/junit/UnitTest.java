import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import tester.annotations.*;
import org.junit.jupiter.api.extension.RegisterExtension;

@Exercises({ @Ex(exID = "Generics", points = 1) })
public class UnitTest {
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test
	@Timeout(value  =  100, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Points(exID = "Generics", bonus = 0.1)
	public void test() { }
}

