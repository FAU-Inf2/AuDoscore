import java.util.*;
import java.lang.*;
import java.lang.reflect.*;
import java.lang.annotation.*;
import org.json.simple.*;
import org.json.simple.parser.*;

// TODO: pfad zu python-skript oder zu den zu checkenden klassen sollte konfigurierbar sein!

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
			//System.err.println("exec'ing " + Arrays.toString(cmd));
			Process proc = Runtime.getRuntime().exec(cmd);
			proc.getInputStream().close();
			proc.getErrorStream().close();
			proc.waitFor();
			return proc.exitValue();
		} catch (Exception e) {
			System.err.println(e.toString());
			System.exit(1);
			return 0;
        }
	}

	private static class check {
		String classname;
		String [] methods;
		String [] tocheck;
		double malus;
		boolean mustNotFind;
		String exID;
		public check(String classname, String[] methods, String[] tocheck, double malus, String exID) {
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
			this(mu.classname(), mu.methods(), mu.notUsable(), mu.malus(), mu.exID());
			this.mustNotFind = true;
		}

		public List<JSONObject> runCheck() {
			ArrayList<JSONObject> rv = new ArrayList<>();
			for(String methodRE : methods) {
				for(String tocheckRE: tocheck) {
					String[] cmd = {
						"./isAccessedInFunction.py",
						classname + ".class",
						methodRE,
						tocheckRE
					};
					int ret = execStuff(cmd);
					if(!mustNotFind && ret == 0 || mustNotFind && ret == 1) {
						// nothing to do
					} else {
						JSONObject x = new JSONObject ();
						x.put("score", (new Double(-malus)).toString());
						x.put("classname", classname);
						x.put("method", methodRE);
						x.put("tocheck", tocheckRE);
						x.put("id", exID);
						x.put("desc", classname + "." + methodRE + " : access of " +  tocheckRE + ( mustNotFind ? " found" : " not found"));
						x.put("error", mustNotFind ? "access found" : "access not found");
						x.put("success", (Boolean) (false));
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
			System.err.println("loading class " + tcln);
			Class newClass = cl.loadClass(tcln);
			UsageRestriction ur = (UsageRestriction) newClass.getAnnotation(UsageRestriction.class);
			if(ur == null) continue;
			for(MustUse mux : ur.mustUse()) {
				ch.add(new check(mux));
			}
			for(MustNotUse mnux : ur.mustNotUse()){
				ch.add(new check(mnux));
			}
		}
		//System.err.println(ch.size() + " checks");
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
