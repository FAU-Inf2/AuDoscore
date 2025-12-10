import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "new_language_features_25_flexible_constructor_bodies", bonus = 0.815, comment = "SecretTest: Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.checkAndCorrect")
	public void secTest__initialize_fields_before_super_invocation() {
		assertTrue(ToTest.checkAndCorrect(42) > 0, "Just to ensure that the method really exists and survives @Replace...");
		assertEquals(42, new ToTest(42).y, "Just to ensure that the field really exists and survives @Replace...");
		assertEquals(42, new ToTest(42).toTest(), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_25_flexible_constructor_bodies", bonus = 0.815, comment = "SecretTest: Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTestNestedOuter.setCounter")
	public void secTest__nested_classes() {
		ToTestNestedOuter toTestNestedOuter = new ToTestNestedOuter();
		assertEquals(4711, toTestNestedOuter.counter, "Just to ensure that the field really exists and survives @Replace...");
		toTestNestedOuter.new ToTestNestedInner();
		assertEquals(42, toTestNestedOuter.counter, "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_25_flexible_constructor_bodies", bonus = 0.815, comment = "PublicTest: Should pass in \"replaced\".")
	public void secTest__record() {
		ToTestRecord<String> r = new ToTestRecord<>("Foo");
		assertEquals("Foo", r.v(), "Should pass in \"replaced\".");
	}
}
