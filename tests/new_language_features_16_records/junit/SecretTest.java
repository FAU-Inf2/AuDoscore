import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "new_language_features_16_records", bonus = 0.815, comment = "SecretTest \"regular\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("Point2D.sum")
	public void secTest_regular() {
		assertEquals(42 + 666, ToTest.toTest_regular(42, 666), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_16_records", bonus = 0.815, comment = "SecretTest \"member\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.getSum_member")
	public void secTest_member() {
		assertEquals(42 + 666, ToTest.toTest_member(42, 666, 4711), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_16_records", bonus = 0.815, comment = "SecretTest \"member_anonymous_inner\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.getSum_member_anonymous_inner")
	public void secTest_member_anonymous_inner() {
		assertEquals(42 + 666, ToTest.toTest_member_anonymous_inner(42, 666, 4711, 0x815), "Should pass in \"replaced\" because with @Replace now.");
	}
}
