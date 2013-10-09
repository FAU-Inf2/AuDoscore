import org.junit.runner.RunWith;
import static org.junit.runners.Suite.*;
import org.junit.experimental.categories.Categories;
import static org.junit.experimental.categories.Categories.*;

interface MainTest {}

@RunWith(Categories.class)
@IncludeCategory(MainTest.class)
@SuiteClasses(ExampleTestcase.class)
class MainTestSuite {
}

@RunWith(Categories.class)
@ExcludeCategory(MainTest.class)
@SuiteClasses(ExampleTestcase.class)
class NonMainTestSuite {
}