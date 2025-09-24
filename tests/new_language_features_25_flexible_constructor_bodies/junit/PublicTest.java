import static org.junit.Assert.*;
import org.junit.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "Flexible_Constructor_Bodies", points = 47.11)})
public class PublicTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 200)
	@Points(exID = "Flexible_Constructor_Bodies", bonus = 0.815, comment = "PublicTest: Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__initialize_fields_before_super_invocation() {
		assertTrue("Just to ensure that the method really exists and survives @Replace...", ToTest.checkAndCorrect(42) > 0);
		assertEquals("Just to ensure that the field really exists and survives @Replace...", 42, new ToTest(42).y);
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42, new ToTest(42).toTest());
	}

	@Test(timeout = 200)
	@Points(exID = "Flexible_Constructor_Bodies", bonus = 0.815, comment = "PublicTest: Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__nested_classes() {
		ToTestNestedOuter toTestNestedOuter = new ToTestNestedOuter();
		assertEquals("Just to ensure that the field really exists and survives @Replace...", 4711, toTestNestedOuter.counter);
		toTestNestedOuter.new ToTestNestedInner();
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 42, toTestNestedOuter.counter);
	}

	@Test(timeout = 200)
	@Points(exID = "Flexible_Constructor_Bodies", bonus = 0.815, comment = "PublicTest: Should pass in \"vanilla\".")
	public void pubTest__record() {
		ToTestRecord<String> r = new ToTestRecord<>("Foo");
		assertEquals("Should pass in \"vanilla\".", "Foo", r.v());
	}
}
