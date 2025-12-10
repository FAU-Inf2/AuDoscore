import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
@org.junit.jupiter.api.TestMethodOrder(org.junit.jupiter.api.MethodOrderer.MethodName.class)
public class SecretTest {
	@Points(exID = "public_fixmethodorder_secret", bonus = 6.5)
	public void secret_b() {
		assertEquals(0, Student.add(2));
	}

	@Points(exID = "public_fixmethodorder_secret", bonus = 0.5)
	public void secret_a() {
		assertEquals(0, Student.add(1));
	}

	@Points(exID = "public_fixmethodorder_secret", bonus = 0.4)
	public void secret_d() {
		assertEquals(0, Student.add(-5));
	}

	@Points(exID = "public_fixmethodorder_secret", malus = 1.4)
	public void secret_c() {
		assertEquals(0, Student.add(39));
	}
}
