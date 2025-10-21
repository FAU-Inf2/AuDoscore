import static org.junit.Assert.*;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import tester.annotations.Points;
import tester.annotations.Replace;
import tester.annotations.SecretClass;

@SecretClass
public class SecretTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 200)
	@Points(exID = "Boxed", bonus = 1, comment = "secTest_toTest_boolean: Should pass in \"replaced\" because with @Replace now.")
	@Replace({"ToTest.toTest_boolean"})
	public void secTest_toTest_boolean() {
		assertFalse("Should pass in \"replaced\" because with @Replace now.", ToTest.toTest_boolean(true, false));
	}

	@Test(timeout = 200)
	@Points(exID = "Boxed", bonus = 1, comment = "secTest_toTest_byte: Should pass in \"replaced\" because with @Replace now.")
	@Replace({"ToTest.toTest_byte"})
	public void secTest_toTest_byte() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 3 + 5, ToTest.toTest_byte((byte) 3, (byte) 5));
	}

	@Test(timeout = 200)
	@Points(exID = "Boxed", bonus = 1, comment = "secTest_toTest_char: Should pass in \"replaced\" because with @Replace now.")
	@Replace({"ToTest.toTest_char"})
	public void secTest_toTest_char() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 3 + 5, ToTest.toTest_char((char) 3, (char) 5));
	}

	@Test(timeout = 200)
	@Points(exID = "Boxed", bonus = 1, comment = "secTest_toTest_short: Should pass in \"replaced\" because with @Replace now.")
	@Replace({"ToTest.toTest_short"})
	public void secTest_toTest_short() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 3 + 5, ToTest.toTest_short((short) 3, (short) 5));
	}

	@Test(timeout = 200)
	@Points(exID = "Boxed", bonus = 1, comment = "secTest_toTest_int: Should pass in \"replaced\" because with @Replace now.")
	@Replace({"ToTest.toTest_int"})
	public void secTest_toTest_int() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 3 + 5, ToTest.toTest_int(3, 5));
	}

	@Test(timeout = 200)
	@Points(exID = "Boxed", bonus = 1, comment = "secTest_toTest_long: Should pass in \"replaced\" because with @Replace now.")
	@Replace({"ToTest.toTest_long"})
	public void secTest_toTest_long() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 3 + 5, (long) ToTest.toTest_long(3L, 5L));
	}

	@Test(timeout = 200)
	@Points(exID = "Boxed", bonus = 1, comment = "secTest_toTest_float: Should pass in \"replaced\" because with @Replace now.")
	@Replace({"ToTest.toTest_float"})
	public void secTest_toTest_float() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 3 + 5, ToTest.toTest_float(3f, 5f), 0.1f);
	}

	@Test(timeout = 200)
	@Points(exID = "Boxed", bonus = 1, comment = "secTest_toTest_double: Should pass in \"replaced\" because with @Replace now.")
	@Replace({"ToTest.toTest_double"})
	public void secTest_toTest_double() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 3 + 5, ToTest.toTest_double(3d, 5d), 0.1d);
	}
}
