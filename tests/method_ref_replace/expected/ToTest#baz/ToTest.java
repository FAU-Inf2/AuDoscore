import java.util.function.IntSupplier;
import java.util.function.IntSupplier;

public class ToTest {
    
    public ToTest() {
        super();
    }
    
    public int foo() {
        return bar(this::baz);
    }
    
    public int bar(final IntSupplier sup) {
        return sup.getAsInt();
    }
    
    public int baz() {
        return 42;
    }
}
