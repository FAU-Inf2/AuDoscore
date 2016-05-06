import java.util.*;
import java.lang.*;
import java.lang.reflect.*;
import java.lang.annotation.*;
import tester.annotations.*;

public class ReadForbidden {
	public static void main(String args[]) throws Exception {
		if (args.length == 0) {
			System.err.println("missing class argument");
			System.exit(-1);
		}

		String grep = "egrep '(java(\\.|/)lang(\\.|/)ClassLoader|java(\\.|/)lang(\\.|/)reflect|java(\\.|/)lang(\\.|/)System(\\.|/)exit";
		String grep2 = "egrep -v '(";
		String sep = "";
		boolean hasNotForbidden = false;
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		for (String tcln : args) {
			Class newClass = cl.loadClass(tcln);
			Forbidden forbidden = (Forbidden) newClass.getAnnotation(Forbidden.class);
			if (forbidden == null) continue;
			for (String s : forbidden.value()) {
				String escape = getRegex(s, forbidden.type());
				grep += "|" + escape;
			}
			NotForbidden notforbidden = (NotForbidden) newClass.getAnnotation(NotForbidden.class);
			if (notforbidden == null) continue;
			for (String s : notforbidden.value()) {
				hasNotForbidden = true;
				String escape = getRegex(s, notforbidden.type());
				grep2 += sep + escape;
				sep = "|";
			}
		}

		grep += ")'";
		grep2 += ")'";
		if(hasNotForbidden) {
			grep = grep2 + " | " + grep;
		}
		System.out.println(grep);
	}

	private static String getRegex(final String classSpec, final Forbidden.Type type) {
		switch (type) {
			case PREFIX:
				return classSpec.replaceAll("\\.", "(\\\\.|/)");

			case FIXED:
				return classSpec.replaceAll("\\.", "(\\\\.|/)") + "\\W";

			case WILDCARD:
				return classSpec.replaceAll("\\.", "(\\\\.|/)").replaceAll("\\*", "\\S*");

			default:
				System.err.println("unsupported type for @Forbidden");
				System.exit(-2);
				return null;
		}
	}
}
