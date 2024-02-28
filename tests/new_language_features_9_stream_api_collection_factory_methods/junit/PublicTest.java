import static org.junit.Assert.*;
import org.junit.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "Lambda", points = 47.11)})
public class PublicTest {
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815, comment = "PublicTest \"toTest__List_of__Stream_takeWhile\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest__List_of__Stream_takeWhile() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 1 + 2 + 3 + 4 + 5 + 6 + 7, ToTest.toTest__List_of__Stream_takeWhile());
	}

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815, comment = "PublicTest \"toTest__List_of__Stream_dropWhile\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest__List_of__Stream_dropWhile() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 8 + 9, ToTest.toTest__List_of__Stream_dropWhile());
	}

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815, comment = "PublicTest \"toTest__Set_of__Stream_filter\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest__Set_of__Stream_filter() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 1 + 2 + 3 + 4 + 5 + 6 + 7, ToTest.toTest__Set_of__Stream_filter());
	}

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815, comment = "PublicTest \"toTest__Map_of\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest__Map_of() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 7 + 7 + 7 + 7 + 7, ToTest.toTest__Map_of());
	}

	@Test(timeout = 200)
	@Points(exID = "Lambda", bonus = 0.815, comment = "PublicTest \"toTest__Stream_iterate_with_condition\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest__Stream_iterate_with_condition() {
		assertEquals("Should fail in \"vanilla\" because without @Replace.", 1 + 2 + 3 + 4 + 5 + 6 + 7, ToTest.toTest__Stream_iterate_with_condition());
	}
}
