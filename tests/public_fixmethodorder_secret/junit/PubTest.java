import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "public_fixmethodorder_secret", points = 5)})
@org.junit.jupiter.api.TestMethodOrder(org.junit.jupiter.api.MethodOrderer.MethodName.class)
public class PubTest {
	@Points(exID = "public_fixmethodorder_secret", bonus = 1.5)
	public void public_b() {
		assertEquals(1, Student.add(2));
	}

	@Points(exID = "public_fixmethodorder_secret", bonus = 0.5)
	public void public_a() {
		assertEquals(0, Student.add(1));
	}

	@Points(exID = "public_fixmethodorder_secret", bonus = 0.4)
	public void public_d() {
		assertEquals(42, Student.add(-5));
	}

	@Points(exID = "public_fixmethodorder_secret", bonus = 9.4)
	public void public_c() {
		assertEquals(3, Student.add(39));
	}
}
