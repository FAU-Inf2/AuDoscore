import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "new_language_features_25_flexible_constructor_bodies", points = 47.11)})
public class PublicTest {
	@Points(exID = "new_language_features_25_flexible_constructor_bodies", bonus = 0.815, comment = "PublicTest: Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__initialize_fields_before_super_invocation() {
		assertTrue(ToTest.checkAndCorrect(42) > 0, "Just to ensure that the method really exists and survives @Replace...");
		assertEquals(42, new ToTest(42).y, "Just to ensure that the field really exists and survives @Replace...");
		assertEquals(42, new ToTest(42).toTest(), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_25_flexible_constructor_bodies", bonus = 0.815, comment = "PublicTest: Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__nested_classes() {
		ToTestNestedOuter toTestNestedOuter = new ToTestNestedOuter();
		assertEquals(4711, toTestNestedOuter.counter, "Just to ensure that the field really exists and survives @Replace...");
		toTestNestedOuter.new ToTestNestedInner();
		assertEquals(42, toTestNestedOuter.counter, "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_25_flexible_constructor_bodies", bonus = 0.815, comment = "PublicTest: Should pass in \"vanilla\".")
	public void pubTest__record() {
		ToTestRecord<String> r = new ToTestRecord<>("Foo");
		assertEquals("Foo", r.v(), "Should pass in \"vanilla\".");
	}
}
