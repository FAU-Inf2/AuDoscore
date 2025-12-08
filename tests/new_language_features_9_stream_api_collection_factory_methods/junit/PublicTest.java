import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@Exercises({@Ex(exID = "new_language_features_9_stream_api_collection_factory_methods", points = 47.11)})
public class PublicTest {
	@Points(exID = "new_language_features_9_stream_api_collection_factory_methods", bonus = 0.815, comment = "PublicTest \"toTest__List_of__stream_takeWhile\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest__List_of__stream_takeWhile() {
		assertEquals(1 + 2 + 3 + 4 + 5 + 6 + 7, ToTest.toTest__List_of__stream_takeWhile(), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_9_stream_api_collection_factory_methods", bonus = 0.815, comment = "PublicTest \"toTest__List_of__parallelStream_dropWhile\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest__List_of__parallelStream_dropWhile() {
		assertEquals(8 + 9, ToTest.toTest__List_of__parallelStream_dropWhile(), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_9_stream_api_collection_factory_methods", bonus = 0.815, comment = "PublicTest \"toTest__Set_of__stream_filter\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest__Set_of__stream_filter() {
		assertEquals(1 + 2 + 3 + 4 + 5 + 6 + 7, ToTest.toTest__Set_of__stream_filter(), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_9_stream_api_collection_factory_methods", bonus = 0.815, comment = "PublicTest \"toTest__Map_of\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest__Map_of() {
		assertEquals(7 + 7 + 7 + 7 + 7, ToTest.toTest__Map_of(), "Should fail in \"vanilla\" because without @Replace.");
	}

	@Points(exID = "new_language_features_9_stream_api_collection_factory_methods", bonus = 0.815, comment = "PublicTest \"toTest__stream_iterate_with_condition_parallel_reduce\": Should fail in \"vanilla\" because without @Replace.")
	public void pubTest__toTest__stream_iterate_with_condition_parallel_reduce() {
		assertEquals(1 + 2 + 3 + 4 + 5 + 6 + 7, ToTest.toTest__stream_iterate_with_condition_parallel_reduce(), "Should fail in \"vanilla\" because without @Replace.");
	}
}
