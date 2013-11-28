package tester;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.lang.reflect.*;

public class ReadReplace{
	public static String getSig(Method m){
		String sig = m.getDeclaringClass().getName() + "." + m.getName() + "(";
		for(Class p : m.getParameterTypes()){
			sig +=  p.getSimpleName() + ", ";
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
		Set<String> liste = new TreeSet<String>();
		for(Method meth : c.getMethods()){
			if(meth.isAnnotationPresent(Replace.class)){
				Map<String, SortedSet<String>> methsMap = new HashMap<String, SortedSet<String>>();
				Replace r = meth.getAnnotation(Replace.class);
				for(int i=0; i<r.value().length; ++i){
					int s = r.value()[i].indexOf('.');
					String cln;
					String regex;
					if(s == -1){
						cln = r.value()[i];
						regex = ".*";
					}else{
						cln = r.value()[i].substring(0, s);
						regex = r.value()[i].substring(s+1);
					}

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
						ncln += "#" + me;
					System.out.print("cp cleanroom/" + e.getKey() + ".java cleanroom/orig_" + e.getKey() + ".java;");
					System.out.print("/bin/echo -e \"package cleanroom;\" > cleanroom/" + e.getKey() + ".java;");
					System.out.print("cat cleanroom/orig_" + e.getKey() + ".java >> cleanroom/" + e.getKey() + ".java;");
					System.out.print("mkdir -p " + ncln + "; ");
					System.out.print("javac -cp .:lib/tools.jar:lib/junit.jar:lib/junitpoints.jar -Areplaces=" + ncln + " -proc:only -processor ReplaceMixer cleanroom/" + e.getKey() + ".java " + e.getKey() + ".java > " + ncln + "/" + e.getKey() + ".java; ");
					System.out.print("javac -cp replaced -d " + ncln + " -sourcepath " + ncln + " " + e.getKey() + ".java;");
					System.out.println("mv cleanroom/orig_" + e.getKey() + ".java cleanroom/" + e.getKey() + ".java;");
				}
			}
		}

		PrintWriter configOut = new PrintWriter(new FileWriter("asp/Config.java", true));
		configOut.println("static Method replacedMethods[] = new Method[" + liste.size() + "];");
		configOut.println("static Map<String, Integer> replacedMap = new HashMap<String, Integer>();\nstatic {");
		int i = 0;

		PrintWriter aspectOut = new PrintWriter(new FileWriter("asp/AllocFactoryAspect.java", true));
		for(String elem : liste) {
			configOut.println("replacedMap.put(\""+elem+"\","+i+");");
			aspectOut.println("pointcut callStatic"+i+"(): call(public static * " + elem + ");");
			aspectOut.println("Object around() : callStatic"+i+"() {");
			aspectOut.println("if(replacedMethods["+i+"] == null)");
			aspectOut.println("	return proceed();");
			aspectOut.println("else");
			aspectOut.println("	try {");
			aspectOut.println("		return replacedMethods["+i+"].invoke(null, thisJoinPoint.getArgs());");
			aspectOut.println("	} catch (InvocationTargetException e){");
			aspectOut.println("	        throw new RuntimeException(e.getTargetException());");
			aspectOut.println("	} catch (IllegalAccessException|IllegalArgumentException e){");
			aspectOut.println("            e.printStackTrace();");
			aspectOut.println("            throw new java.lang.annotation.AnnotationFormatError(\"internal error while invoking method\");");
			aspectOut.println("	}");
			aspectOut.println("}");
			i++;
		}
		configOut.println("}}");
		configOut.close();
		aspectOut.println("}\n");
		aspectOut.close();
	}
}
