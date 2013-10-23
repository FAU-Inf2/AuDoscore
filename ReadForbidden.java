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

		String grep = "egrep '(java/lang/ClassLoader|java\\.lang\\.ClassLoader|java/lang/reflect|java\\.lang\\.reflect";

		ClassLoader cl = ClassLoader.getSystemClassLoader();
		for (String tcln : args) {
			Class newClass = cl.loadClass(tcln);
			Forbidden forbidden = (Forbidden) newClass.getAnnotation(Forbidden.class);
			for (String s : forbidden.value()) {
				String escape = s.replaceAll("\\.", "\\\\.");
				grep += "|" + escape;
				escape = s.replaceAll("\\.", "/");
				grep += "|" + escape;
			}
		}

		grep += ")'";
		System.out.println(grep);
	}
}
