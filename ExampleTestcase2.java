// Zusatzklasse um UsageRestriction zu testen
// instruktionen:
/*
   ( cd tester && javac *.java )
   javac -cp ./tester:.:./lib/json-simple-1.1.1.jar  CheckMustUse.java ExampleTestcase2.java
   java -cp ./tester:.:./lib/json-simple-1.1.1.jar  CheckMustUse ExampleTestcase2 
*/
// erwarteter output: {"deductions":[{"tocheck":"Field yy:","error":"access found","malus":2,"exID":"foo1Fail","classname":"foo","method":"getxx\\("},{"tocheck":"Field xx:","error":"access not found","malus":2,"exID":"foo2Fail","classname":"foo","method":"getxx\\("}]}

@UsageRestriction(
   mustUse = {
	   @MustUse(classname="foo", methods={"getxx\\("}, usable={"Field xx:"}, malus=2, exID="foo1Succ"),
	   @MustUse(classname="foo", methods={"getxx\\("}, usable={"Field yy:"}, malus=2, exID="foo1Fail")
   },
   mustNotUse={
	   @MustNotUse(classname="foo", methods={"getxx\\("}, notUsable={"Field yy:"}, malus=2, exID="foo2Succ"),
	   @MustNotUse(classname="foo", methods={"getxx\\("}, notUsable={"Field xx:"}, malus=2, exID="foo2Fail")
   }
)
public class ExampleTestcase2 {
	public static void main(String[] args) {
		System.out.println(-4 + (int) 2.6);
		System.out.println((int)(-4 + 2.6));
		foo.yy = 12;
		foo f = new foo();
		f.xx = 13;
		f.yy = 14;
		f.setxx(15);
		System.out.println(f.getxx());
	}
}

class foo {
	int xx;
	static int yy;
	int zz;
	private int aa;
	int getxx() { return xx; }
	void setxx(int xx) { this.xx = xx; }
	void bar(int a, String b, int cc) {}
}