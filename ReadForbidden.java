import java.lang.annotation.*;
import tester.annotations.*;

public class ReadForbidden {
	public static void main(String args[]) throws Exception {
		if (args.length == 0) {
			System.err.println("missing class argument");
			System.exit(-1);
		}

		final StringBuilder grep = new StringBuilder(
			"egrep '(java(\\.|/)lang(\\.|/)ClassLoader|java(\\.|/)lang(\\.|/)reflect|java(\\.|/)lang(\\.|/)System(\\.|/)exit|java(\\.|/)awt(\\.|/)|javax(\\.|/)swing(\\.|/)"
		);
		final StringBuilder grep2 = new StringBuilder("egrep -v '(");
		String sep = "";
		boolean hasNotForbidden = false;
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		for (String tcln : args) {
			Class newClass = cl.loadClass(tcln);
			Forbidden forbidden = (Forbidden) newClass.getAnnotation(Forbidden.class);
			if (forbidden == null) {
				continue;
			}
			for (String s : forbidden.value()) {
				grep.append('|').append(getRegex(s, forbidden.type()));
			}

			NotForbidden notforbidden = (NotForbidden) newClass.getAnnotation(NotForbidden.class);
			if (notforbidden == null) {
				continue;
			}
			for (String s : notforbidden.value()) {
				hasNotForbidden = true;
				grep2.append(sep).append("(\\W|:L|\\[L)").append(getRegex(s, notforbidden.type()));
				sep = "|";
			}
		}

		grep.append(")'");
		grep2.append(")'");

		String result = grep.toString();
		if(hasNotForbidden) {
			result = grep2.toString() + " | " + result;
		}
		System.out.println(result);
	}

	private static String getRegex(final String classSpec, final Forbidden.Type type) {
		switch (type) {
			case PREFIX:
				return classSpec.replaceAll("\\.", "(\\\\.|/)");

			case FIXED:
				return classSpec.replaceAll("\\.", "(\\\\.|/)") + "(\\W|$)";

			case WILDCARD:
				return classSpec.replaceAll("\\.", "(\\\\.|/)").replaceAll("\\*", "[^\\\\./\\\\s]*");

			default:
				System.err.println("unsupported type for @Forbidden");
				System.exit(-2);
				return null;
		}
	}
}
