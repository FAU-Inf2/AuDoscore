package tester;

import java.io.FilePermission;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ReflectPermission;
import java.net.NetPermission;
import java.util.Collections;
import java.util.List;
import java.util.PropertyPermission;
import java.security.Permission;



public class TesterSecurityManager extends SecurityManager {

	private final List<String> safeCallerList;



	public TesterSecurityManager(final List<String> safeCallerList) {
		this.safeCallerList = safeCallerList;
	}



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
					// Only grant this permission if the method is called by a safe
					// caller, JUnit, by java.lang.Thread.<init>, or by the lambda
					// metafactory (Java 8)
					final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
					for (int i = 1; i < stackTrace.length; ++i) {
						if (this.safeCallerList.contains(stackTrace[i].getClassName())) {
							// Grant permission
							return;
						}

						if (!this.getClass().getCanonicalName().equals(stackTrace[i].getClassName())
								&& !"tools.SingleMethodRunner".equals(stackTrace[i].getClassName())
								&& !stackTrace[i].getClassName().startsWith("java.")
								&& !stackTrace[i].getClassName().startsWith("sun.reflect.")
								&& !stackTrace[i].getClassName().startsWith("org.junit.")) {
							// Also grant the permission if the first offending method is a
							// JUnit test case
							try {
								final Method method = Class.forName(stackTrace[i].getClassName())
										.getMethod(stackTrace[i].getMethodName());
								if (method != null) {
									for (final Annotation annotation : method.getDeclaredAnnotations()) {
										if ("org.junit.Test".equals(annotation.annotationType().getCanonicalName())) {
											// Called from JUnit test case, grant permission
											return;
										}
									}
								}
							} catch (final NoSuchMethodException|ClassNotFoundException e) {
								// Ignore
							}

							// Deny permission
							super.checkPermission(perm);
						}
						if (stackTrace[i].getClassName().startsWith(
									"java.lang.invoke.InnerClassLambdaMetafactory")) {
							// lambda metafactory, grant permission
							return;
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

				case "createClassLoader": {
					// Only grant this permission if the method is called from JUnit
					if (calledFromJUnit()
							|| calledFromSafeCallers()
							|| calledFrom("java.awt.Color", "java.")) {
						return;
					}
					break;
				}

				case "loadLibrary.awt": {
					if (calledFrom("java.awt.Color", "java.")) {
						return;
					}
					break;
				}

				case "getenv.DISPLAY": {
					if (calledFrom("java.awt.Toolkit", "java.")) {
						// Grant permission
						return;
					}
					break;
				}

				default: {
					if (perm.getName().startsWith("loadLibrary.") && perm.getName().contains("awt")
							&& calledFrom("java.awt.Toolkit", "java.")) {
						// Grant permission
						return;
					}
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
			if ("sun.font.fontmanager".equals(propPerm.getName())
					&& calledFrom("java.awt.Toolkit", "java.")) {
				// Grant permission
				return;
			}
		} else if (perm instanceof NetPermission) {
			if (!checkNetPermission((NetPermission) perm)) {
				super.checkPermission(perm);
			}
			// Grant permission
			return;
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
		// Allow only if called from a safe caller, ClassLoader or from JUnit
		final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (int i = 1; i < stackTrace.length; ++i) {
			if (this.safeCallerList.contains(stackTrace[i].getClassName())) {
				// Allow
				return true;
			}

			if (!stackTrace[i].getClassName().startsWith("java.")
					&& !stackTrace[i].getClassName().startsWith("sun.")
					&& !stackTrace[i].getClassName().startsWith("org.junit.")
					&& !this.getClass().getCanonicalName().equals(stackTrace[i].getClassName())) {
				// Deny read
				return false;
			}
			if ("java.lang.ClassLoader".equals(stackTrace[i].getClassName())
					|| stackTrace[i].getClassName().startsWith("java.lang.ClassLoader$")) {
				// Allow
				return true;
			}
			if ("org.junit.runner.JUnitCore".equals(stackTrace[i].getClassName())) {
				// Allow
				return true;
			}
		}

		return false;
	}



	private boolean checkNetPermission(final NetPermission perm) {
		if ("specifyStreamHandler".equals(perm.getName())) {
			// Allow only if called from a safe caller or JUnit
			return calledFromJUnit()
					|| calledFromSafeCallers()
					|| calledFrom("java.awt.Toolkit", "java.", "sun.");
		}
		return false;
	}



	private boolean calledFromSafeCallers() {
		return calledFrom(this.safeCallerList, "java.", "sun.", "org.junit.");
	}



	private boolean calledFromJUnit() {
		return calledFrom("org.junit.runner.JUnitCore", "java.", "sun.", "org.junit.");
	}



	private boolean calledFrom(final String calledFromClassName,
			final String ... allowedClassPrefixes) {
		return calledFrom(Collections.singletonList(calledFromClassName), allowedClassPrefixes);
	}



	private boolean calledFrom(final List<String> calledFromNames,
			final String ... allowedClassPrefixes) {

		final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (int i = 1; i < stackTrace.length; ++i) {
			if (!this.getClass().getCanonicalName().equals(stackTrace[i].getClassName())) {
				boolean checkNext = true;
				for (int j = 0; checkNext && j < allowedClassPrefixes.length; ++j) {
					checkNext &= !stackTrace[i].getClassName().startsWith(allowedClassPrefixes[j]);
				}

				if (checkNext) {
					return false;
				}
			}

			if (calledFromNames.contains(stackTrace[i].getClassName())) {
				return true;
			}
		}
		return false;
	}
}

