package tester;

import tester.annotations.*;
import java.util.*;
import java.lang.reflect.*;
import java.lang.annotation.*;
import org.junit.runner.*;

public class ReadReplace {

	public static String getCanonicalReplacement(Replace r) {
		Map<String, SortedSet<String>> mMethsMap = getMap(r);
		String ncln = "";
		for(Map.Entry<String, SortedSet<String>> e : mMethsMap.entrySet()) {
			ncln += "@" + e.getKey();
			for(String me : e.getValue())
				ncln += "#" + me;
		}
		return ncln;
	}

	public static Map<String, SortedSet<String>> getMap(Replace r) {
		Map<String, SortedSet<String>> mMethsMap = new TreeMap<>();
		for(int i=0; i<r.value().length; ++i) {
			int s = r.value()[i].indexOf('.');
			String cln;
			String regex;
			if(s == -1) {
				cln = r.value()[i];
				regex = ".*";
			} else {
				cln = r.value()[i].substring(0, s);
				regex = r.value()[i].substring(s+1);
			}

			if (!mMethsMap.containsKey(cln))
				mMethsMap.put(cln, new TreeSet<String>());
			SortedSet<String> meths = mMethsMap.get(cln);

			try {
				for (Method me : Class.forName(cln).getDeclaredMethods()) {
					if (me.getName().matches(regex)) {
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
		if (description.getAnnotation(Replace.class) != null) {
			Replace r = description.getAnnotation(Replace.class);
			return getCanonicalReplacement(r);
		}
		return "";
	}

	public static void loopSecret(String tcln, String pub) throws Exception {
		HashMap<String,List<String>> rmap = new HashMap<String,List<String>>();
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		Class c = cl.loadClass(tcln);
		for (Method meth : c.getMethods()) {
			if (meth.isAnnotationPresent(Replace.class)) {
				Replace r = meth.getAnnotation(Replace.class);
				String cr = getCanonicalReplacement(r);
				List<String> methods = rmap.get(cr);
				if (methods == null) {
					methods = new ArrayList<String>();
				}
				methods.add(meth.getName());
				rmap.put(cr, methods);
			}
		}

		boolean needSep = false;
		Iterator it = rmap.entrySet().iterator();
		while(it.hasNext()) {
			if (needSep) {
				System.out.println("echo \",\" 1>&2");
			} else {
				needSep = true;
			}
			Map.Entry pair = (Map.Entry) it.next();
			String s = (String) pair.getKey();
			List<String> methods = (List<String>) pair.getValue();
			String classpath = s.substring(1).replaceAll("@", ":").replaceAll("<", "\\\\<").replaceAll(">", "\\\\>");
		
			boolean first = true;
			for(String method : methods) {
				if(first) {
					first = false;
				}else{
					System.out.println("echo \",\" 1>&2");
				}
				System.out.println("java -XX:+UseConcMarkSweepGC -Xmx1024m -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:" + classpath + ":. -Dpub=" +pub+" -Djson=yes tools.SingleMethodRunner " + tcln + " "  + method + " || echo");
			}

		}
	}

	public static void main(String args[]) throws Exception{

		if(args.length < 1){
			System.err.println("missing class argument");
			System.exit(-1);
		}
		if (args[0].equals("--loop")) {
			loopSecret(args[3],args[2]);
			return;
		}

		String tcln = args[0];
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		Class c = cl.loadClass(tcln);
		HashSet<String> pres = new HashSet<>();
		HashSet<String> mids = new HashSet<>();
		HashSet<String> posts = new HashSet<>();
		for (Method meth : c.getMethods()) {
			if (meth.isAnnotationPresent(Replace.class)) {
				Replace r = meth.getAnnotation(Replace.class);
				Map<String, SortedSet<String>> methsMap = getMap(r);
				for (Map.Entry<String, SortedSet<String>> e : methsMap.entrySet()) {
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
