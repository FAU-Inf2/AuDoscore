public class ToTest implements IToTest {
	public int toTest() {
		return getSome_default();
	}

	@Override // TODO: @Replace gives "INTERNAL ERROR" if student does NOT also @Override this method!
	public int getSome_default() {
		return IToTest.getSome_static();
	}
}
