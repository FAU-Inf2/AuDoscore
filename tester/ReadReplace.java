package tester;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.lang.reflect.*;

public class ReadReplace{
	public static String getSig(Method m){
		String sig = m.getDeclaringClass().getName() + "." + m.getName() + "(";
		for(Class p : m.getParameterTypes()){
			sig +=  p.getName() + ", ";
		}
		if(m.getParameterTypes().length > 0){
			sig = sig.substring(0, sig.length()-2);
		}
		return sig + ")";
	}
	public static void main(String args[]) throws Exception{
		if(args.length != 1){
			System.err.println("missing class argument");
			System.exit(-1);
		}
		String tcln = args[0];
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		Class c = cl.loadClass(tcln);
		List<String> liste = new ArrayList<String>();
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
							if((me.getModifiers() & Modifier.STATIC) != 0){
								liste.add(getSig(me));
							}
						}
					}
				}
				for(Map.Entry<String, SortedSet<String>> e : methsMap.entrySet()){
					String ncln = e.getKey();
					if(e.getValue().size() == 0)
						continue;
					for(String me : e.getValue())
						ncln += "_" + me;
					System.out.print("java -jar lib/parser.jar cleanroom/" + e.getKey() + ".java " + e.getKey());
					for(String me : e.getValue())
						System.out.print(" " + me);
					System.out.println("; mv cleanroom/" + e.getKey() + ".java.pretty mixed/" + ncln + ".java\n");
				}
			}
		}

		PrintWriter configOut = new PrintWriter(new FileWriter("asp/Config.java", true));
		configOut.println("static Method replacedMethods[] = new Method[" + liste.size() + "];");
		configOut.println("static Map<String, Integer> replacedMap = new HashMap<String, Integer>();\nstatic {");
		for(int i=0; i<liste.size(); ++i){
			configOut.println("replacedMap.put(\""+liste.get(i)+"\","+i+");");
			System.err.println("pointcut callStatic"+i+"(): call(public static * " + liste.get(i) + ");");
			System.err.println("Object around() : callStatic"+i+"() {");
			System.err.println("if(replacedMethods["+i+"] == null)");
			System.err.println("	proceed();");
			System.err.println("else");
			System.err.println("	try {");
			System.err.println("		return replacedMethods["+i+"].invoke(null, thisJoinPoint.getArgs());");
			System.err.println("	} catch (Exception e){");
			System.err.println("	}");
			System.err.println("	return proceed();");
			System.err.println("}");
		}
		configOut.println("}}");
		configOut.close();
		System.err.print("}\n");
	}
}
