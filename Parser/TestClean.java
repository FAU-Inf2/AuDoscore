public class TestClean extends String {

	private void nobodylikesthisfirstmethod() {
	}

	public TestClean() {
		// noargs
	}
	public TestClean(int arg1, char arg2) {
		// args
	}
	private TestClean(int /* nasty  */
			arg1, char 
			
			arg2
			
			) {
		// nasty
	}

	public int foo() throws Exception {
		return 23;
	}

	public int bar() {
		/* this is correct */
		return 4711;
	}


	public int bar(WTF wtf) {
		int ret = 42;
		/* some code */
		return ret;
	}

	class Inner extends Whatever {
		public int bar(Inner inn) {
			return inn.toInt();
		}
	}

	public int foo = 42;
	private final int __clean_foo = 43;
	public String __clean_clean;
}

class Brother {
	public int bar() {
		return brother;
	}
}
