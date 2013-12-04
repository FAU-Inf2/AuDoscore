package tester;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.lang.reflect.*;
import java.lang.annotation.*;
import org.junit.*;
import org.junit.rules.*;
import org.junit.runner.*;
import org.junit.runners.model.*;

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

	public static String getCanonicalReplacement(Replace r) {
		Map<String, SortedSet<String>> mMethsMap = getMap(r);
		String ncln = "";
		for(Map.Entry<String, SortedSet<String>> e : mMethsMap.entrySet()){
			ncln += "@" + e.getKey();
			for(String me : e.getValue())
				ncln += "#" + me;
		}
		return ncln;
	}

	public static Map<String, SortedSet<String>> getMap(Replace r) {
		Map<String, SortedSet<String>> mMethsMap = new TreeMap<>();
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

			if(!mMethsMap.containsKey(cln))
				mMethsMap.put(cln, new TreeSet<String>());
			SortedSet<String> meths = mMethsMap.get(cln);

			try {
				for(Method me : Class.forName(cln).getDeclaredMethods()){
					if(me.getName().matches(regex)){
						meths.add(me.getName());
					}
				}
				if ("<init>".matches(regex)) {
					meths.add("<init>");
				}
			} catch (ClassNotFoundException e) {
				throw new AnnotationFormatError("Cannot replace unknown class: " + cln);
			}
		}
		return mMethsMap;
	}

	public static String getCanonicalReplacement(Description description) {
		if(description.getAnnotation(Replace.class) != null) {
			Replace r = description.getAnnotation(Replace.class);
			return getCanonicalReplacement(r);
		}
		return "";
	}

	public static void loop(String args[]) throws Exception {
		HashSet<String> set = new HashSet<>();

		String tcln = args[1];
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		Class c = cl.loadClass(tcln);
		for(Method meth : c.getMethods()) {
			if(meth.isAnnotationPresent(Replace.class)){
				Replace r = meth.getAnnotation(Replace.class);
				set.add(getCanonicalReplacement(r));
			}
		}

		System.out.println("echo \"[\" 1>&2");
		System.out.println("java -XX:+UseConcMarkSweepGC -Xmx1024m -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:" + "--THIS-WILL-NEVER-HAPPEN" + ":.  -Dreplace=" + "--THIS-WILL-NEVER-HAPPEN" + " -Djson=yes org.junit.runner.JUnitCore " + tcln + " || echo");
		for (String s : set) {
			System.out.println("echo \",\" 1>&2");
			String classpath = s.substring(1).replaceAll("@", ":").replaceAll("<", "\\\\<").replaceAll(">", "\\\\>");
			System.out.println("java -XX:+UseConcMarkSweepGC -Xmx1024m -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:" + classpath + ":.  -Dreplace=" + s.replaceAll("<", "\\\\<").replaceAll(">", "\\\\>") + " -Djson=yes org.junit.runner.JUnitCore " + tcln + " || echo");
		}
		System.out.println("echo \"]\" 1>&2");
	}

	public static void main(String args[]) throws Exception{
		if(args.length < 1){
			System.err.println("missing class argument");
			System.exit(-1);
		}
		if (args[0].equals("--loop")) {
			loop(args);
			return;
		}
		String tcln = args[0];
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		Class c = cl.loadClass(tcln);
		HashSet<String> pres = new HashSet<>();
		HashSet<String> mids = new HashSet<>();
		HashSet<String> posts = new HashSet<>();
		for(Method meth : c.getMethods()) {
			if(meth.isAnnotationPresent(Replace.class)){
				Replace r = meth.getAnnotation(Replace.class);
				Map<String, SortedSet<String>> methsMap = getMap(r);
				for(Map.Entry<String, SortedSet<String>> e : methsMap.entrySet()){
					String ncln = e.getKey();
					if(e.getValue().size() == 0)
						continue;
					for(String me : e.getValue())
						ncln += "#" + me.replaceAll("<", "\\\\<").replaceAll(">", "\\\\>");
					pres.add("cp cleanroom/" + e.getKey() + ".java cleanroom/orig_" + e.getKey() + ".java; "
						+ "/bin/echo -e \"package cleanroom;\" > cleanroom/" + e.getKey() + ".java; "
						+ "cat cleanroom/orig_" + e.getKey() + ".java >> cleanroom/" + e.getKey() + ".java;");
					mids.add("mkdir -p " + ncln + "; "
						+ "javac -cp .:lib/tools.jar:lib/junit.jar:lib/junitpoints.jar -Areplaces=" + ncln + " -proc:only -processor ReplaceMixer cleanroom/" + e.getKey() + ".java " + e.getKey() + ".java > " + ncln + "/" + e.getKey() + ".java; "
						+ "javac -cp . -d " + ncln + " -sourcepath " + ncln + " " + ncln + "/" + e.getKey() + ".java;");
					posts.add("mv cleanroom/orig_" + e.getKey() + ".java cleanroom/" + e.getKey() + ".java;");
				}
			}
		}
		for (String pre : pres) System.out.println(pre);
		for (String mid : mids) System.out.println(mid);
		for (String post : posts) System.out.println(post);
	}
}
