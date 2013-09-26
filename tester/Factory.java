package tester;
import java.util.*;
import java.lang.reflect.*;
public class Factory{
	public static Map<Class, Class> mClassMap;
	
	public static boolean isKnown(Class c){
		if(mClassMap == null)
			return false;
		return mClassMap.containsKey(c);
	}

	static Object getInstance(Class c, Object... o) {
		Class[] param = new Class[o.length];
		for(int i=0; i<o.length; ++i){
			if(o[i] == null)
				param[i] = null;
			else
				param[i] = o[i].getClass();
		}
		return getInstance(c, param, o);
	}
	
	@SuppressWarnings("unchecked")
	public static Object getInstance(Class c, Class paramt[], Object paramo[]) {
		try{
			if(mClassMap == null)
				return null;
			if(mClassMap.containsKey(c))
				c = mClassMap.get(c);
			else
				return null;

			Constructor[] cons = c.getDeclaredConstructors();
			for(int i=0; i<cons.length; ++i){
				cons[i].setAccessible(true);
			}
			if(paramt.length == 0){
				return cons[0].newInstance();
			}
			Constructor con = c.getConstructor(paramt);
			con.setAccessible(true);
			return con.newInstance(paramo);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
