import java.util.*;
import java.lang.*;
import java.lang.reflect.*;
import java.lang.annotation.*;

public class ReadForbidden {
	public static void main(String args[]) throws Exception {
		if (args.length == 0) {
			System.err.println("missing class argument");
			System.exit(-1);
		}

		String grep = "egrep '(java/lang/ClassLoader|java\\.lang\\.ClassLoader|java/lang/reflect|java\\.lang\\.reflect|java/lang/System\\.exit";
		String grep2 = "egrep -v '(";
		String sep = "";
		boolean hasNotForbidden = false;
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		for (String tcln : args) {
			Class newClass = cl.loadClass(tcln);
			Forbidden forbidden = (Forbidden) newClass.getAnnotation(Forbidden.class);
			if (forbidden == null) continue;
			for (String s : forbidden.value()) {
				String escape = s.replaceAll("\\.", "(\\\\.|/)");
				grep += "|" + escape;
			}
			NotForbidden notforbidden = (NotForbidden) newClass.getAnnotation(NotForbidden.class);
			if (notforbidden == null) continue;
			for (String s : notforbidden.value()) {
				hasNotForbidden = true;
				String escape = s.replaceAll("\\.", "(\\\\.|/)");
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
}
