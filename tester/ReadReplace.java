package tester;

import tester.annotations.*;
import java.util.*;
import java.lang.reflect.*;
import java.lang.annotation.*;
import org.junit.runner.*;

public class ReadReplace {

	public static Method[] getMethodsSorted(final Class cls) {
		final Method[] methods = cls.getMethods();
		Arrays.sort(methods, new Comparator<Method>() {
			@Override
			public int compare(final Method method1, final Method method2) {
				return method1.getName().compareTo(method2.getName());
			}
		});
		return methods;
	}

	public static String getCanonicalReplacement(Replace r) {
		Map<String, SortedSet<String>> mMethsMap = getMap(r);
		final StringBuilder ncln = new StringBuilder();
		for(Map.Entry<String, SortedSet<String>> e : mMethsMap.entrySet()) {
			ncln.append('@').append(e.getKey());
			for(String me : e.getValue()) {
				ncln.append('#').append(me);
			}
		}
		return ncln.toString();
	}

	public static String getCanonicalReplacement(Description description) {
		if (description.getAnnotation(Replace.class) != null) {
			Replace r = description.getAnnotation(Replace.class);
			return getCanonicalReplacement(r);
		}
		return "";
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

			if (!mMethsMap.containsKey(cln)) {
				mMethsMap.put(cln, new TreeSet<String>());
			}
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
				throw new AnnotationFormatError("ERROR - Cannot replace unknown class: " + cln);
			}
		}
		return mMethsMap;
	}

	public static void loopSecret(String tcln, String pub) throws Exception {
		LinkedHashMap<String,List<String>> rmap = new LinkedHashMap<String,List<String>>();
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		Class c = cl.loadClass(tcln);
		for (Method meth : getMethodsSorted(c)) {
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
				System.out.println("java -XX:-OmitStackTraceInFastThrow -XX:+UseConcMarkSweepGC -Xmx1024m -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:" + classpath + ":. -Dpub=" +pub+" -Djson=yes tools.SingleMethodRunner " + tcln + " "  + method);
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
		LinkedHashSet<String> mids = new LinkedHashSet<>();
		for (Method meth : getMethodsSorted(c)) {
			if (meth.isAnnotationPresent(Replace.class)) {
				Replace r = meth.getAnnotation(Replace.class);
				Map<String, SortedSet<String>> methsMap = getMap(r);
				for (Map.Entry<String, SortedSet<String>> e : methsMap.entrySet()) {
					final StringBuilder ncln = new StringBuilder(e.getKey());
					if(e.getValue().size() == 0) {
						continue;
					}
					for(String me : e.getValue()) {
						ncln.append('#').append(me.replaceAll("<", "\\\\<").replaceAll(">", "\\\\>"));
					}
					final String nclns = ncln.toString();
					mids.add("mkdir -p " + nclns + "; "
							+ "javac -Xprefer:source -cp .:lib/junit.jar:lib/junitpoints.jar -Areplaces="
							+ nclns
							+ " -proc:only -processor ReplaceMixer cleanroom/"
							+ e.getKey() + ".java "
							+ e.getKey() + ".java > "
							+ nclns
							+ "/"
							+ e.getKey() + ".java; "
							+ "javac -cp . -d "
							+ nclns
							+ " -sourcepath "
							+ nclns
							+ " "
							+ nclns
							+ "/"
							+ e.getKey() + ".java;");
				}
			}
		}
		for (String mid : mids) {
			System.out.println(mid);
		}
	}
}
