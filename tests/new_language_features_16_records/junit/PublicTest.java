import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "new_language_features_16_records", points = 47.11)})
public class PublicTest {
	@Points(exID = "new_language_features_16_records", bonus = 0.815, comment = "PublicTest \"regular\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_regular() {
		assertEquals(42 + 666, ToTest.toTest_regular(42, 666), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_16_records", bonus = 0.815, comment = "PublicTest \"member\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_member() {
		assertEquals(42 + 666, ToTest.toTest_member(42, 666, 4711), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_16_records", bonus = 0.815, comment = "PublicTest \"member_anonymous_inner\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest_member_anonymous_inner() {
		assertEquals(42 + 666, ToTest.toTest_member_anonymous_inner(42, 666, 4711, 0x815), "Should fail in \"vanilla\" because without @Replace.");
	}
}
