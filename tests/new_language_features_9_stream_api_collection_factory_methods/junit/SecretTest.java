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
	@Points(exID = "Lambda", bonus = 0.815, comment = "SecretTest \"toTest__List_of__Stream_takeWhile\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest__List_of__Stream_takeWhile")
	public void secTest__toTest__List_of__Stream_takeWhile() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 1 + 2 + 3 + 4 + 5 + 6 + 7, ToTest.toTest__List_of__Stream_takeWhile());
	}

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815, comment = "SecretTest \"toTest__List_of__Stream_dropWhile\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest__List_of__Stream_dropWhile")
	public void secTest__toTest__List_of__Stream_dropWhile() {
		assertEquals("Should pass in \"vanilla\" because without @Replace.", 8 + 9, ToTest.toTest__List_of__Stream_dropWhile());
	}

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815, comment = "SecretTest \"toTest__Set_of__Stream_filter\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest__Set_of__Stream_filter")
	public void secTest__toTest__Set_of__Stream_filter() {
		assertEquals("Should pass in \"replaced\" because with @Replace now.", 1 + 2 + 3 + 4 + 5 + 6 + 7, ToTest.toTest__Set_of__Stream_filter());
	}

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815, comment = "SecretTest \"toTest__Map_of\": Should pass in \"vanilla\" because without @Replace.")
	@Replace("ToTest.toTest__Map_of")
	public void secTest__toTest__Map_of() {
		assertEquals("Should pass in \"vanilla\" because without @Replace.", 7 + 7 + 7 + 7 + 7, ToTest.toTest__Map_of());
	}

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815, comment = "SecretTest \"toTest__Stream_iterate_with_condition\": Should pass in \"vanilla\" because without @Replace.")
	@Replace("ToTest.toTest__Stream_iterate_with_condition")
	public void secTest__toTest__Stream_iterate_with_condition() {
		assertEquals("Should pass in \"vanilla\" because without @Replace.", 1 + 2 + 3 + 4 + 5 + 6 + 7, ToTest.toTest__Stream_iterate_with_condition());
	}
}
