import org.junit.rules.TestName;
import org.junit.Rule;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.*;
import java.lang.reflect.*;
import asp.*;
import tester.*;

class A{
	int x = 0;
	int m0(){
		return 0;
	}
	int m1(){
		return 1;
	}
}

class A_m0 extends A{
	A_m0(){
		x = 1;
	}
	int m0(){
		return 10 * x;
	}
}

class C extends A{
	int x = 5;
	int m0(){
		return x;
	}
}

class C_m0 extends C{
	int m0(){
		C.super.m0();
	}
}

class B{
	int m0(){
		return 100;
	}
	int m1(){
		return 101;
	}
	int m2(){
		return 102;
	}
	public String toMyString() {
		return super.toString();
	}
}

class B_m1_m2 extends B{
	int m1(){
		return 111;
	}
	int m2(){
		return 112;
	}
}

class B_m1_m2_toMyString extends B{
	int m1(){
		return 111;
	}
	int m2(){
		return 112;
	}
	public String toMyString() {
		return "falsch";
	}
}

class B_m0 extends B{
	int m0(){
		return 120;
	}
}

class B_m0_m1_m2_toMyString extends B{
	int m0(){
		return 130;
	}
	int m1(){
		return 131;
	}
	int m2(){
		return 132;
	}
	public String toMyString() {
		return "falsch";
	}
}

class C {
	int[] ia;
	Integer[] iia;
	public C(int[] ia, Integer[] iia){
		this.ia = ia;
		this.iia = iia;
	}
	public void m0(){
		ia[0] = 1;
	}
	public int m1(){
		return iia[0];
	}

	void test(Number n, Integer i){
	}

	void test(Integer n, Integer i){
	}
}

class C_m0_m1 extends C{
	public C_m0_m1(int[] ia, Integer[] iia){
		super(ia, iia);
	}
	public void m0(){
		ia[0] = 2;
	}
	public int m1(){
		return ia[0];
	}
}


public class Tester{
	@Rule public TestName name = new TestName();
	
	@Before
	public void before() throws Exception{
		String mn = name.getMethodName();
		Method m = Tester.class.getMethod(mn);
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		Factory.mClassMap = new HashMap<Class, Class>();
		if(m.isAnnotationPresent(Replace.class)){
			Replace r = m.getAnnotation(Replace.class);
			for(int i=0; i<r.value().length; ++i){
				int s = r.value()[i].indexOf('.');
				String cln = r.value()[i].substring(0, s);

				String regex = r.value()[i].substring(s+1);
				
				ArrayList<String> meths = new ArrayList<String>();
				for(Method me : cl.loadClass(cln).getDeclaredMethods()){
					if(me.getName().matches(regex)){
						meths.add(me.getName());
					}
				}

				Collections.sort(meths);

				String ncln = cln;
				for(String me : meths)
					ncln += "_" + me;
				Factory.mClassMap.put(cl.loadClass(cln), cl.loadClass(ncln));
			}
		}
	}

	/*
	@After
	public void after() throws Throwable {
		String mn = name.getMethodName();
		Method m = Tester.class.getMethod(mn);
		Factory.mClassMap = null;
		try{
			m.invoke(this);
		} catch (InvocationTargetException e){
			if(e.getCause() != null)
				throw e.getCause(); 
			else
				throw e;
		}
	}
	*/

	@Test
	@Replace({"A.m0", "B..*"})
	public void test0(){
		//A a = Factory.getInstance(A.class, (A)null);
		A a = new A();
		assertEquals("", 10, a.m0());
		assertEquals("", 1, a.m1());
		B b = new B();
		assertEquals("", 130, b.m0());
		assertEquals("", 131, b.m1());
		assertEquals("", 132, b.m2());
	}

	@Test
	@Replace({"B.m0"})
	public void test1(){
		//A a = Factory.getInstance(A.class, (A)null);
		A a = new A();
		assertEquals("", 0, a.m0());
		assertEquals("", 1, a.m1());
		//ArrayList<Integer> al = new ArrayList<>();
		//ArrayList<Integer> al = Factory.getInstance(ArrayList.class, (ArrayList)null);
	}

	@Test
	@Replace({"B.[mt].*"})
	public void testS(){
		B b = new B();
		System.out.println(b.toMyString());
		assertEquals("", true, b.toMyString().equals("falsch"));
	}
	
	@Test
	@Replace({})
	public void testS2(){
		B b = new B();
		assertEquals("", false, b.toMyString().equals("falsch"));
	}
	
	/*
	@Test
	@Replace({"C.m."})
	public void test2(){
		System.out.println("hier");
		C c = new C(new int[]{4, 5}, new Integer[]{-3});
		c.test(new Integer(3), null);
		assertEquals("", 4, c.m1());
		c = new C(null, null);
	}
	*/
}
