package tester;

import java.io.FilePermission;
import java.lang.reflect.ReflectPermission;
import java.util.PropertyPermission;
import java.security.Permission;



public class TesterSecurityManager extends SecurityManager {
	@Override
	public void checkAccess(final Thread t) {
		// Allow access to threads
	}



	@Override
	public void checkAccess(final ThreadGroup g) {
		// Allow access to threadgroups
	}



	@Override
	public void checkExec(final String cmd) {
		// Forbid execution of processes
		super.checkExec(cmd);
	}



	@Override
	public void checkPackageAccess(final String pkg) {
		// Allow access to packages
	}



	@Override
	public void checkPermission(final Permission perm) {
		if (perm instanceof RuntimePermission) {
			switch (((RuntimePermission) perm).getName()) {
				case "setIO":
				case "modifyThread":
				case "stopThread":
				case "modifyThreadGroup":
				case "getProtectionDomain":
				case "getStackTrace": {
					// Grant these permissions
					return;
				}

				case "accessDeclaredMembers": {
					// Only grant this permission if the method is called by JUnit or by
					// java.lang.Thread.<init>
					final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
					for (int i = 1; i < stackTrace.length; ++i) {
						if (!this.getClass().getCanonicalName().equals(stackTrace[i].getClassName())
								&& !"tools.SingleMethodRunner".equals(stackTrace[i].getClassName())
								&& !stackTrace[i].getClassName().startsWith("java.")
								&& !stackTrace[i].getClassName().startsWith("org.junit.")) {
							// Deny permission
							super.checkPermission(perm);
						}
						if ("java.lang.Thread".equals(stackTrace[i].getClassName())
								&& "<init>".equals(stackTrace[i].getMethodName())) {
							// Thread constructor, grant permission
							return;
						}
					}
					// No violating class found, grant permission
					return;
				}

				case "setSecurityManager": {
					// Only grant this permission if the method is called from
					// the class JUnitWithPoints.PointsLogger
					boolean allowed = true;
					final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
					for (int i = 1; i < stackTrace.length
							&& "JUnitWithPoints$PointsLogger".equals(stackTrace[i].getClassName()); ++i) {
						allowed &= stackTrace[i].getClassName().startsWith("java.");
					}
					if (allowed) {
						// Grant the permission
						return;
					}
					break;
				}
			}
		} else if (perm instanceof FilePermission) {
			final FilePermission filePerm = (FilePermission) perm;
			if ("read".equals(filePerm.getActions())) {
				if (!checkReadPermissions(filePerm.getName())) {
					super.checkPermission(perm);
				}
				// Grant permission
				return;
			}
		} else if (perm instanceof PropertyPermission) {
			final PropertyPermission propPerm = (PropertyPermission) perm;
			if ("read".equals(propPerm.getActions())) {
				// Grant permission
				return;
			}
		} else if (perm instanceof ReflectPermission) {
			// Reflection is already checked during stage1, so allow everything
			return;
		}
		// Deny all other permissions
		super.checkPermission(perm);
	}



	@Override
	public void checkRead(final String file) {
		if (!checkReadPermissions(file)) {
			super.checkRead(file);
		}
	}



	@Override
	public void checkWrite(final String file) {
		// Forbid writing to a file
		super.checkWrite(file);
	}



	private boolean checkReadPermissions(final String fileName) {
		// Allow only if called from ClassLoader
		final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (int i = 1; i < stackTrace.length; ++i) {
			if (!stackTrace[i].getClassName().startsWith("java.")
					&& !stackTrace[i].getClassName().startsWith("sun.")
					&& !this.getClass().getCanonicalName().equals(stackTrace[i].getClassName())) {
				// Deny read
				return false;
			}
			if ("java.lang.ClassLoader".equals(stackTrace[i].getClassName())) {
				// Allow
				return true;
			}
		}

		return false;
	}
}

