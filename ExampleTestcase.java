import org.junit.*;
import org.junit.rules.*;
import static org.junit.Assert.*;
import java.lang.reflect.*;
import java.lang.*;
import java.util.*;
import java.io.*;
import asp.*;
import tester.*;

@Exercises({ @Ex(exID = "GA4.6a", points = 12.5), @Ex(exID = "GA4.6b", points = 3.0), @Ex(exID = "GA4.6c", points = 44) })
public class ExampleTestcase {
	// instead of explicitly coding the following rules here,
	// your test class can also just extend the class JUnitWithPoints
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();
	@Rule public TestName testcaseName = new TestName();

	private PrintStream saveOut;
	private PrintStream saveErr;

	@Before
	public void before() throws Exception{
		saveOut = System.out;
		saveErr = System.err;
		System.setOut(new PrintStream(new OutputStream() {
			public void write(int i) {
			}
		}));

		System.setErr(new PrintStream(new OutputStream() {
			public void write(int i) {
			}
		}));

		String doReplace = System.getProperty("replace");
		if(doReplace == null || !doReplace.equals("yes"))
			return;
		String mn = testcaseName.getMethodName();
		Method m = getClass().getMethod(mn);
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		Factory.mClassMap = new HashMap<Class, Class>();
		Factory.mMethsMap = new HashMap<String, SortedSet<String>>();
		if(m.isAnnotationPresent(Replace.class)){
			Replace r = m.getAnnotation(Replace.class);
			for(int i=0; i<r.value().length; ++i){
				int s = r.value()[i].indexOf('.');
				String cln = r.value()[i].substring(0, s);

				String regex = r.value()[i].substring(s+1);
				
				if(!Factory.mMethsMap.containsKey(cln))
					Factory.mMethsMap.put(cln, new TreeSet<String>());
				SortedSet<String> meths = Factory.mMethsMap.get(cln);

				for(Method me : cl.loadClass(cln).getDeclaredMethods()){
					if(me.getName().matches(regex)){
						meths.add(me.getName());
					}
				}
			}
			for(Map.Entry<String, SortedSet<String>> e : Factory.mMethsMap.entrySet()){
				String ncln = e.getKey();
				for(String me : e.getValue())
					ncln += "_" + me;
				Factory.mClassMap.put(cl.loadClass(e.getKey()), cl.loadClass(ncln));
			}
		}
	}
	
	@After
	public void after() throws Exception{
		System.setOut(saveOut);
		System.setErr(saveErr);
	}

	@Test
	@Bonus(exID = "GA4.6a", bonus = 47.11)
	@Replace({"Student.foo"})
	public void testFooShouldReturn4711() { // OK
		Student sut = new Student();
		assertEquals("Foo ist kaputt.", 4711, sut.foo());
	}

	@Test
	@Replace({"Student.foo"})
	@Bonus(exID = "GA4.6b", bonus = 47.11, comment = "Bar should return 0.815 here.")
	public void testBarShouldReturn0815() { // FAILS
		Student sut = new Student();
		assertEquals("Bar ist kaputt.", 0.815, sut.bar(), 1e-3);
	}

	@Test
	@Replace({"Student.baz"})
	@Malus(exID = "GA4.6b", malus = 8, comment = "Check if GA4.6b respects Blatt-00...")
	public void testBazShouldBeNice() { // OK
		Student sut = new Student();
		assertEquals("RTFM - Blatt-00!", "I am nice.", sut.baz());
	}

	@Test
	@Malus(exID = "GA4.6a", malus = 42.0815, comment = "Check if GA4.6a respects Blatt-00.")
	public void testFooBarShouldBeNice() { // FAILS
		Student sut = new Student();
		assertEquals("see Blatt-00: Thou shalt not use packages.", "I am nice.", sut.foobar());
	}

	@Test
	@Malus(exID = "GA4.6c", malus = 1, comment = "NPE2-Test: if you know a variable is null, you should not access it later on!")
	public void testNPE2() {
		Student.getNull().toString();
	}

	@Test
	@Replace({"Student.doNull"})
	@Bonus(exID = "GA4.6c", bonus = 1, comment = "NPE3-Test")
	public void testNPE3() {
		(new Student()).doNull();
	}

	@Test
	@Bonus(exID = "GA4.6c", bonus = 1, comment = "NPE-Test")
	public void testNPE() {
		String s = null;
		s.toString();
	}

	@Test
	@Bonus(exID = "GA4.6c", bonus = 1, comment = "IOOB-Test")
	public void testIOOB() {
		int a[] = new int[20];
		a[21] = a[0] + a[-1];
	}

	@Test
	@Bonus(exID = "GA4.6c", bonus = 1, comment = "IOOB-Test")
	public void testIOOB2() {
		Student.ioob();
	}

	@Test
	@Bonus(exID = "GA4.6c", bonus = 1, comment = "nix")
	public void testNix() {
	}

	@Test
	@Bonus(exID = "GA4.6c", bonus = 1, comment = "sof")
	public void testSOF() {
		Student.recur();
	}
}
