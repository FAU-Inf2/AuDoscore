import static org.junit.jupiter.api.Assertions.*;
import tester.annotations.*;

@SecretClass
public class SecretTest {
	@Points(exID = "new_language_features_9_stream_api_collection_factory_methods", bonus = 0.815, comment = "SecretTest \"toTest__List_of__stream_takeWhile\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest__List_of__stream_takeWhile")
	public void secTest__toTest__List_of__stream_takeWhile() {
		assertEquals(1 + 2 + 3 + 4 + 5 + 6 + 7, ToTest.toTest__List_of__stream_takeWhile(), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_9_stream_api_collection_factory_methods", bonus = 0.815, comment = "SecretTest \"toTest__List_of__parallelStream_dropWhile\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest__List_of__parallelStream_dropWhile")
	public void secTest__toTest__List_of__parallelStream_dropWhile() {
		assertEquals(8 + 9, ToTest.toTest__List_of__parallelStream_dropWhile(), "Should pass in \"replaced\" because without @Replace.");
	}

	@Points(exID = "new_language_features_9_stream_api_collection_factory_methods", bonus = 0.815, comment = "SecretTest \"toTest__Set_of__stream_filter\": Should pass in \"replaced\" because with @Replace now.")
	@Replace("ToTest.toTest__Set_of__stream_filter")
	public void secTest__toTest__Set_of__stream_filter() {
		assertEquals(1 + 2 + 3 + 4 + 5 + 6 + 7, ToTest.toTest__Set_of__stream_filter(), "Should pass in \"replaced\" because with @Replace now.");
	}

	@Points(exID = "new_language_features_9_stream_api_collection_factory_methods", bonus = 0.815, comment = "SecretTest \"toTest__Map_of\": Should pass in \"replaced\" because without @Replace.")
	@Replace("ToTest.toTest__Map_of")
	public void secTest__toTest__Map_of() {
		assertEquals(7 + 7 + 7 + 7 + 7, ToTest.toTest__Map_of(), "Should pass in \"replaced\" because without @Replace.");
	}

	@Points(exID = "new_language_features_9_stream_api_collection_factory_methods", bonus = 0.815, comment = "SecretTest \"toTest__stream_iterate_with_condition_parallel_reduce\": Should pass in \"replaced\" because without @Replace.")
	@Replace("ToTest.toTest__stream_iterate_with_condition_parallel_reduce")
	public void secTest__toTest__stream_iterate_with_condition_parallel_reduce() {
		assertEquals(1 + 2 + 3 + 4 + 5 + 6 + 7, ToTest.toTest__stream_iterate_with_condition_parallel_reduce(), "Should pass in \"replaced\" because without @Replace.");
	}
}
