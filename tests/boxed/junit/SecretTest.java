import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "boxed", bonus = 1, comment = "secTest_toTest_boolean: Should pass in \"replaced\" because with @Replace now.")
	@Replace({"ToTest.toTest_boolean"})
	public void secTest_toTest_boolean() {
		assertFalse(ToTest.toTest_boolean(true, false), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "boxed", bonus = 1, comment = "secTest_toTest_byte: Should pass in \"replaced\" because with @Replace now.")
	@Replace({"ToTest.toTest_byte"})
	public void secTest_toTest_byte() {
		assertEquals(3 + 5, ToTest.toTest_byte((byte) 3, (byte) 5), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "boxed", bonus = 1, comment = "secTest_toTest_char: Should pass in \"replaced\" because with @Replace now.")
	@Replace({"ToTest.toTest_char"})
	public void secTest_toTest_char() {
		assertEquals(3 + 5, ToTest.toTest_char((char) 3, (char) 5), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "boxed", bonus = 1, comment = "secTest_toTest_short: Should pass in \"replaced\" because with @Replace now.")
	@Replace({"ToTest.toTest_short"})
	public void secTest_toTest_short() {
		assertEquals(3 + 5, ToTest.toTest_short((short) 3, (short) 5), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "boxed", bonus = 1, comment = "secTest_toTest_int: Should pass in \"replaced\" because with @Replace now.")
	@Replace({"ToTest.toTest_int"})
	public void secTest_toTest_int() {
		assertEquals(3 + 5, ToTest.toTest_int(3, 5), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "boxed", bonus = 1, comment = "secTest_toTest_long: Should pass in \"replaced\" because with @Replace now.")
	@Replace({"ToTest.toTest_long"})
	public void secTest_toTest_long() {
		assertEquals(3 + 5, (long) ToTest.toTest_long(3L, 5L), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "boxed", bonus = 1, comment = "secTest_toTest_float: Should pass in \"replaced\" because with @Replace now.")
	@Replace({"ToTest.toTest_float"})
	public void secTest_toTest_float() {
		assertEquals(3 + 5, ToTest.toTest_float(3f, 5f), 0.1f, "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "boxed", bonus = 1, comment = "secTest_toTest_double: Should pass in \"replaced\" because with @Replace now.")
	@Replace({"ToTest.toTest_double"})
	public void secTest_toTest_double() {
		assertEquals(3 + 5, ToTest.toTest_double(3d, 5d), 0.1d, "Should pass in \"replaced\" because with @Replace now.");
	}
}
