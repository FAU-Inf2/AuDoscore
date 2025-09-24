import static org.junit.Assert.*;
import org.junit.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 200)
	@Points(exID = "Flexible_Constructor_Bodies", bonus = 0.815, comment = "SecretTest: Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.checkAndCorrect")
	public void secTest__initialize_fields_before_super_invocation() {
		assertTrue("Just to ensure that the method really exists and survives @Replace...", ToTest.checkAndCorrect(42) > 0);
		assertEquals("Just to ensure that the field really exists and survives @Replace...", 42, new ToTest(42).y);
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42, new ToTest(42).toTest());
	}

	@Test(timeout = 200)
	@Points(exID = "Flexible_Constructor_Bodies", bonus = 0.815, comment = "SecretTest: Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTestNestedOuter.setCounter")
	public void secTest__nested_classes() {
		ToTestNestedOuter toTestNestedOuter = new ToTestNestedOuter();
		assertEquals("Just to ensure that the field really exists and survives @Replace...", 4711, toTestNestedOuter.counter);
		toTestNestedOuter.new ToTestNestedInner();
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 42, toTestNestedOuter.counter);
	}

	@Test(timeout = 200)
	@Points(exID = "Flexible_Constructor_Bodies", bonus = 0.815, comment = "PublicTest: Should pass in \"replaced\".")
	public void secTest__record() {
		ToTestRecord<String> r = new ToTestRecord<>("Foo");
		assertEquals("Should pass in \"replaced\".", "Foo", r.v());
	}
}
