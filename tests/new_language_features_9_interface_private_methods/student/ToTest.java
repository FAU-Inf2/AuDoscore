public class ToTest implements IToTest {
	@Override // TODO: @Replace gives "INTERNAL ERROR" if student does NOT also @Override this method!
	public int toTest_default() {
		return IToTest.toTest_static__BUT_DIFFERENT_FROM_CLEAN();
	}
}
