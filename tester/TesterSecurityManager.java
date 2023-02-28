package tester;

import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.security.*;

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
	public void checkAccess(final ThreadGroup threadGroup) {
		// Allow access to thread-groups
	}

	@Override
	public void checkExec(final String command) {
		// Forbid execution of processes
		super.checkExec(command);
	}

	@Override
	public void checkPackageAccess(final String pkg) {
		// Allow access to packages
	}

	@Override
	public void checkPermission(final Permission perm) {
		if (perm instanceof RuntimePermission) {
			switch (perm.getName()) {
				case "setIO", "modifyThread", "stopThread", "modifyThreadGroup", "getProtectionDomain", "getStackTrace" -> {
					// grant these permissions
					return;
				}
				case "accessDeclaredMembers" -> {
					// only grant this permission if the method is called by
					// a safe caller, JUnit, by java.lang.Thread.<init>, or by the lambda meta-factory
					final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
					for (int i = 1; i < stackTrace.length; ++i) {
						if (this.safeCallerList.contains(stackTrace[i].getClassName())) {
							// grant permission to safe callers
							return;
						}
						if (!this.getClass().getCanonicalName().equals(stackTrace[i].getClassName()) //
								&& !"tools.SingleMethodRunner".equals(stackTrace[i].getClassName()) //
								&& !stackTrace[i].getClassName().startsWith("java.") //
								&& !stackTrace[i].getClassName().startsWith("sun.reflect.") //
								&& !stackTrace[i].getClassName().startsWith("org.junit.")) {
							// also grant the permission if the first offending method is a JUnit test case
							try {
								final Method method = Class.forName(stackTrace[i].getClassName()).getMethod(stackTrace[i].getMethodName());
								for (final Annotation annotation : method.getDeclaredAnnotations()) {
									if ("org.junit.Test".equals(annotation.annotationType().getCanonicalName())) {
										// called from JUnit test case => grant permission
										return;
									}
								}
							} catch (final NoSuchMethodException | ClassNotFoundException ignored) {
								// ignore
							}
							// deny permission
							super.checkPermission(perm);
						}
						if (stackTrace[i].getClassName().startsWith("java.lang.invoke.InnerClassLambdaMetafactory")) {
							// lambda meta-factory => grant permission
							return;
						}
						if ("java.lang.Thread".equals(stackTrace[i].getClassName()) && "<init>".equals(stackTrace[i].getMethodName())) {
							// thread constructor => grant permission
							return;
						}
					}
					// no violating class found => grant permission
					return;
				}
				case "setSecurityManager" -> {
					// only grant this permission if the method is called from the class JUnitWithPoints.PointsLogger
					boolean allowed = true;
					final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
					for (int i = 1; allowed && i < stackTrace.length && !"JUnitWithPoints$PointsLogger".equals(stackTrace[i].getClassName()); ++i) {
						allowed = stackTrace[i].getClassName().startsWith("java.") || this.getClass().getCanonicalName().equals(stackTrace[i].getClassName());
					}
					if (allowed) {
						// grant the permission
						return;
					}
				}
				case "accessSystemModules", "createClassLoader", "getClassLoader", "localeServiceProvider" -> {
					// only grant this permission if the method is called from JUnit or from the locale initialization mechanism
					if (calledFromJUnit() || calledFromSafeCallers() || calledFromInitOnce() //
							|| calledFrom("java.text.NumberFormat", "sun.", "java.", "jdk.") //
							|| calledFrom("java.awt.Color", "java.")) {
						return;
					}
				}
				case "loadLibrary.awt" -> {
					if (calledFrom("java.awt.Color", "java.")) {
						return;
					}
				}
				case "loadLibrary.management_ext" -> {
					// might be needed for management API
					if (calledFromSafeCallers()) {
						return;
					}
				}
				case "getenv.DISPLAY" -> {
					if (calledFrom("java.awt.Toolkit", "java.")) {
						// grant permission
						return;
					}
				}
				default -> {
					if (perm.getName().startsWith("loadLibrary.")) {
						if (perm.getName().contains("awt") && calledFrom("java.awt.Toolkit", "java.")) {
							// grant permission
							return;
						}
						if (calledFromSafeCallers()) {
							// grant permission
							return;
						}
					}
					if (perm.getName().startsWith("sun.management.") && calledFromSafeCallers()) {
						// grant permission
						return;
					}
				}
			}
		} else if (perm instanceof final FilePermission filePerm) {
			if ("read".equals(filePerm.getActions())) {
				if (checkReadPermissions(filePerm.getName())) {
					// grant permission
					return;
				} else {
					// deny
					super.checkPermission(perm);
				}
			} else if ("write".equals(filePerm.getActions()) && calledFromInitOnce()) {
				// @InitializeOnce => grant permission
				return;
			}
		} else if (perm instanceof final PropertyPermission propPerm) {
			if ("read".equals(propPerm.getActions())) {
				// grant permission
				return;
			}
			if (("write".equals(propPerm.getActions()) //
					|| "read,write".equals(propPerm.getActions())) && (calledFromSafeCallers() //
					|| calledFrom("java.lang.invoke.StringConcatFactory", "java.", "sun."))) {
				// grant permission
				return;
			}
			if ("sun.font.fontmanager".equals(propPerm.getName()) && calledFrom("java.awt.Toolkit", "java.")) {
				// grant permission
				return;
			}
		} else if (perm instanceof NetPermission) {
			if (!checkNetPermission()) {
				super.checkPermission(perm);
			}
			// grant permission
			return;
		} else if (perm instanceof final SecurityPermission secPerm) {
			if (secPerm.getName().startsWith("getProperty.") && calledFromSafeCallers()) {
				// grant permission
				return;
			} else if ("getProperty.jdk.serialFilter".equals(secPerm.getName()) || "getProperty.jdk.serialFilterFactory".equals(secPerm.getName())) {
				// serialization => grant permission
				return;
			}
		} else if (perm instanceof ReflectPermission) {
			// reflection is already checked during stage1, so allow everything
			return;
		}
		// deny all other permissions
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
		// check for @InitializeOnce
		if (!calledFromInitOnce()) {
			super.checkWrite(file);
		}
	}

	private boolean checkReadPermissions(final String fileName) {
		// check for @InitializeOnce
		if (calledFromInitOnce()) {
			// allow
			return true;
		}
		// allow only if called from a safe caller, ClassLoader, for locale initialization, or from JUnit
		final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (int i = 1; i < stackTrace.length; ++i) {
			if (this.safeCallerList.contains(stackTrace[i].getClassName())) {
				// allow
				return true;
			}
			if (!stackTrace[i].getClassName().startsWith("java.") //
					&& !stackTrace[i].getClassName().startsWith("javax.") //
					&& !stackTrace[i].getClassName().startsWith("jdk.internal.") //
					&& !stackTrace[i].getClassName().startsWith("sun.") //
					&& !stackTrace[i].getClassName().startsWith("com.sun.") //
					&& !stackTrace[i].getClassName().startsWith("org.junit.") //
					&& !this.getClass().getCanonicalName().equals(stackTrace[i].getClassName())) {
				// deny
				return false;
			}
			if ("java.lang.ClassLoader".equals(stackTrace[i].getClassName()) //
					|| stackTrace[i].getClassName().startsWith("java.lang.ClassLoader$")) {
				// allow
				return true;
			}
			if ("java.text.NumberFormat".equals(stackTrace[i].getClassName()) && (fileName.endsWith("localedata.jar") //
					|| fileName.endsWith("currency.data") //
					|| fileName.endsWith("currency.properties"))) {
				// allow
				return true;
			}
			if ("org.junit.runner.JUnitCore".equals(stackTrace[i].getClassName())) {
				// allow
				return true;
			}
		}
		return false;
	}

	private boolean checkNetPermission() {
		// allow only if called from a safe caller or JUnit
		return calledFromJUnit() || calledFromSafeCallers() //
				|| calledFrom("java.text.NumberFormat", "java.", "sun.") //
				|| calledFrom("java.awt.Toolkit", "java.", "sun.");
	}

	private boolean calledFromInitOnce() {
		return calledFrom("JUnitWithPoints$PointsLogger$1InitOnceStatement", "java.", "sun.", "org.junit.", "jdk.internal.");
	}

	private boolean calledFromSafeCallers() {
		return calledFrom(this.safeCallerList, "java.", "javax.", "com.sun.", "sun.", "org.junit.", "jdk.internal.", "jdk.management.");
	}

	private boolean calledFromJUnit() {
		return calledFrom("org.junit.runner.JUnitCore", "java.", "sun.", "org.junit.", "jdk.internal.");
	}

	private boolean calledFrom(final String calledFromClassName, final String... allowedClassPrefixes) {
		return calledFrom(Collections.singletonList(calledFromClassName), allowedClassPrefixes);
	}

	private boolean calledFrom(final List<String> calledFromNames, final String... allowedClassPrefixes) {
		final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (int i = 1; i < stackTrace.length; ++i) {
			if (calledFromNames.contains(stackTrace[i].getClassName())) {
				return true;
			}
			if (!this.getClass().getCanonicalName().equals(stackTrace[i].getClassName())) {
				boolean checkNext = true;
				for (int j = 0; checkNext && j < allowedClassPrefixes.length; ++j) {
					checkNext = !stackTrace[i].getClassName().startsWith(allowedClassPrefixes[j]);
				}
				if (checkNext) {
					return false;
				}
			}
		}
		return true;
	}
}
