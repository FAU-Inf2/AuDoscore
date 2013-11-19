import java.util.*;
import java.lang.*;
import java.lang.reflect.*;
import java.lang.annotation.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class CheckMustUse {
	
	/**
	 * Executes cmd. If Execution finishes with an error status != 0 or if any other exception occurs,
	 * deletes all files mentioned in filesToCleanOnFailure.
	 * 
	 * @param cmd The Command to execute
	 * @param filesToCleanOnFailure The files to delete if execution of cmd fails.  
	 */
	public static int execStuff(String[] cmd) {
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			proc.getInputStream().close();
			proc.getErrorStream().close();
			proc.waitFor();
			return proc.exitValue();
		} catch (Exception e) {
			System.exit(1);
        }
	}

	private static class check {
		String classname;
		String [] methods;
		String [] tocheck;
		int malus;
		boolean mustNotFind;
		String exID;
		public check(String classname, String[] methods, String[] tocheck, int malus, String exID) {
			this.classname = classname;
			this.methods = methods;
			this.tocheck = tocheck;
			this.malus = malus;
			this.exID = exID;
			this.mustNotFind = false;
		}

		public check(MustUse mu) {
			this(mu.classname(), mu.methods(), mu.usable(), mu.malus(), mu.exID());
		}

		public check(MustNotUse mu) {
			this(mu.classname(), mu.methods(), mu.usable(), mu.malus(), mu.exID());
			this.mustNotFind = true;
		}

		public List<JSONObject> runCheck() {
			ArrayList<JSONObject> rv = new ArrayList<>();
			for(String methodRE : methods) {
				for(String tocheckRE: tocheck) {
					String[] cmd = {
						"isAccessedInFunction.py",
						classname + ".class",
						methodRE,
						tocheckRE
					};
					int ret = execStuff(cmd);
					if(!mustNotFind && ret == 0 || mustNotFind && ret != 1) {
						// nothing to do
					} else {
						JSONObject x = new JSONObject ();
						x.put("malus", malus);
						x.put("classname", classname);
						x.put("method", methodRE);
						x.put("tocheck", tocheckRE);
						x.put("exID", exID);
						x.put("error", mustNotFind ? "access not found" : "access found");
						rv.add(x);
					}
				}
			} 
			return rv;
		}
	}
	public static void main(String args[]) throws Exception {
		if (args.length == 0) {
			System.err.println("missing class argument");
			System.exit(-1);
		}

		ArrayList<check> ch = new ArrayList<>();
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		for (String tcln : args) {
			Class newClass = cl.loadClass(tcln);
			MustUse mux = (MustUse) newClass.getAnnotation(MustUse.class);
			if (mux != null) {
				ch.add(new check(mux));
			}
			MustNotUse mnux = (MustNotUse) newClass.getAnnotation(MustNotUse.class);
			if (mnux != null) {
				ch.add(new check(mux));
			}
		}
		JSONArray xx = new JSONArray();
		for(check c : ch) {
			List<JSONObject> jo = c.runCheck();
			xx.addAll(jo);
		}
		JSONObject verdict = new JSONObject();
		verdict.put("deductions", xx);
		System.out.println(verdict);
	}
}
