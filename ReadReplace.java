import java.util.*;
import java.lang.*;
import java.lang.reflect.*;

public class ReadReplace{
	public static void main(String args[]) throws Exception{
		if(args.length != 1){
			System.out.println("class argument");
			System.exit(1);
		}
		String tcln = args[0];
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		Class c = cl.loadClass(tcln);
		for(Method meth : c.getMethods()){
			if(meth.isAnnotationPresent(Replace.class)){
				Map<String, SortedSet<String>> methsMap = new HashMap<String, SortedSet<String>>();
				Replace r = meth.getAnnotation(Replace.class);
				for(int i=0; i<r.value().length; ++i){
					int s = r.value()[i].indexOf('.');
					String cln = r.value()[i].substring(0, s);

					String regex = r.value()[i].substring(s+1);

					if(!methsMap.containsKey(cln))
						methsMap.put(cln, new TreeSet<String>());
					SortedSet<String> meths = methsMap.get(cln);

					for(Method me : cl.loadClass(cln).getDeclaredMethods()){
						if(me.getName().matches(regex)){
							meths.add(me.getName());
						}
					}
				}
				for(Map.Entry<String, SortedSet<String>> e : methsMap.entrySet()){
					String ncln = e.getKey();
					for(String me : e.getValue())
						ncln += "_" + me;
					System.out.print("java -jar lib/parser.jar cleanroom/" + e.getKey() + ".java " + e.getKey());
					for(String me : e.getValue())
						System.out.print(" " + me);
					System.out.println("; mv cleanroom/" + e.getKey() + ".java.pretty mixed/" + ncln + ".java\n");
				}
			}
		}
	}
}
