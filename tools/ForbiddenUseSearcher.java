package tools;

import java.io.IOException;
import java.lang.classfile.*;
import java.lang.classfile.instruction.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import tester.annotations.Forbidden;
import tester.annotations.NotForbidden;

public class ForbiddenUseSearcher {
	static void main(String[] args) {
		var rootDir = System.getProperty("user.dir");
		search(rootDir, args[0]);
	}

	private static final String[] DEFAULT_NOT_FORBIDDEN = {//
			"java.lang.Object**", "java.lang.(Boolean**|Byte**|Character**|Double**|Float**|Integer**|Long**|Short**|Number**)", // wrapper
			"java.lang.(String|StringBuffer|StringBuilder).**", //
			"java.lang.(Comparable)**", //
			"java.lang.(Enum|Record)**", //
			"java.lang.(Math|StrictMath).**", //
			"java.lang.(Exception|Throwable).**", //
			"java.lang.(ArrayIndexOutOfBoundsException|AssertionError|IllegalArgumentException|IllegalStateException|IndexOutOfBoundsException).**", //
			"java.lang.(MatchException|NullPointerException|NumberFormatException|RuntimeException).**", //
			"java.lang.ScopedValue.**", //
			"java.lang.System.(arraycopy|currentTimeMillis|nanoTime).**", //
			"java.lang.(Thread|ThreadLocal)**", //
			"java.io.PrintStream.print*", // for System.[out|err].println
			"java.io.(StringReader|StringWriter).**", //
			"java.math.**", //
			"java.util.**", //
			"java.lang.Class.desiredAssertionStatus**", //
	};

	public static void search(String rootDir, String pubTestName) {
		var testAnnotations = getTestAnnotations(rootDir, pubTestName);
		var studentClassDir = FileSystems.getDefault().getPath(rootDir, "student");
		try (var studentClassFiles = Files.walk(studentClassDir, 1).filter(f -> f.toString().endsWith(".class"))) {
			List<Finding> findings = new LinkedList<>();
			for (Path studentClassFile : studentClassFiles.toList()) {
				ClassModel cm = ClassFile.of().parse(Files.readAllBytes(studentClassFile));
				cm.superclass().map(superClass -> superClass.asInternalName().replace('/', '.')) //
						.filter(usedName -> isForbidden(testAnnotations, usedName)) //
						.ifPresent(usedName -> findings.add(new Finding(FindingType.EXTENDS, studentClassFile, -1, usedName)));
				int lineNumber = 0;
				for (MethodModel mm : cm.methods()) {
					for (CodeElement codeElement : mm.code().map(CompoundElement::elementList).orElse(List.of())) {
						if (codeElement instanceof LineNumber ln) {
							lineNumber = ln.line();
						} else if (codeElement instanceof InvokeInstruction i) {
							String usedName = i.owner().asInternalName().replace('/', '.') + "." + i.name().stringValue() + "(" + i.type().stringValue() + ")";
							if (isForbidden(testAnnotations, usedName)) {
								findings.add(new Finding(FindingType.INVOKES, studentClassFile, lineNumber, usedName));
							}
						} else if (codeElement instanceof NewMultiArrayInstruction i) {
							String usedName = i.arrayType().asInternalName().replace('/', '.');
							while (usedName.startsWith("[")) {
								usedName = usedName.substring(1);
							}
							usedName = usedName.substring(1); // type of base type: "[[I" for int[][] or "[[L" for object[][]...
							if (isForbidden(testAnnotations, usedName)) {
								findings.add(new Finding(FindingType.ARRAY_TYPE, studentClassFile, lineNumber, usedName));
							}
						} else if (codeElement instanceof NewReferenceArrayInstruction i) {
							String usedName = i.componentType().asInternalName().replace('/', '.');
							while (usedName.startsWith("[")) {
								usedName = usedName.substring(1);
							}
							usedName = usedName.substring(1); // type of base type: "[[I" for int[][] or "[[L" for object[][]...
							if (isForbidden(testAnnotations, usedName)) {
								findings.add(new Finding(FindingType.ARRAY_TYPE, studentClassFile, lineNumber, usedName));
							}
						}
					}
				}
			}
			if (!findings.isEmpty()) {
				Comparator<Finding> compareByFileName = Comparator.comparing(f -> f.studentClassFile.getFileName().toString(), String::compareTo);
				findings.sort(compareByFileName.thenComparing(f -> f.lineNumber, Integer::compareTo));
				System.out.println("FOUND FORBIDDEN CODE:");
				findings.forEach(System.out::println);
			}
		} catch (IOException e) {
			System.out.println("could not access student solution classes in folder \"student\"");
			System.exit(-3);
		}

	}

	private record TestAnnotations(List<Forbidden> forbidden, List<NotForbidden> notForbidden) {
	}

	private enum FindingType {
		EXTENDS, INVOKES, ARRAY_TYPE
	}

	private record Finding(FindingType findingType, Path studentClassFile, int lineNumber, String usedName) {
		@Override
		public String toString() {
			return switch (findingType) {
				case EXTENDS -> "# " + studentClassFile.getFileName().toString() + ": extends " + usedName;
				case INVOKES -> "# " + studentClassFile.getFileName().toString() + ":" + lineNumber + ": invokes " + usedName;
				case ARRAY_TYPE -> "# " + studentClassFile.getFileName().toString() + ":" + lineNumber + ": array of " + usedName;
			};
		}
	}

	private static TestAnnotations getTestAnnotations(String rootDir, String pubTestName) {
		var junitClassDir = FileSystems.getDefault().getPath(rootDir, "junit");
		try (var junitClassFiles = Files.walk(junitClassDir, 1).filter(f -> f.toString().endsWith(".class"))) {
			var junitClassFileURLs = junitClassFiles.map(f -> {
				try {
					return f.getParent().toUri().toURL();
				} catch (MalformedURLException e) {
					throw new RuntimeException(e);
				}
			}).toArray(URL[]::new);
			try (var urlClassLoader = new URLClassLoader(junitClassFileURLs)) {
				var pubTest = urlClassLoader.loadClass(pubTestName);
				var forbidden = Arrays.stream(pubTest.getAnnotations()).filter(a -> a instanceof Forbidden).map(a -> (Forbidden) a).toList();
				var notForbidden = Arrays.stream(pubTest.getAnnotations()).filter(a -> a instanceof NotForbidden).map(a -> (NotForbidden) a).toList();
				return new TestAnnotations(forbidden, notForbidden);
			} catch (ClassNotFoundException e) {
				System.out.println("could not access/load public test class \"" + pubTestName + "\" in folder \"junit\"");
				System.exit(-2);
			}
		} catch (IOException e) {
			System.out.println("could not access test case classes in folder \"junit\"");
			System.exit(-2);
		}
		return new TestAnnotations(List.of(), List.of());
	}

	private static boolean matchesForbiddenOrNotForbidden(Forbidden.Type forbiddenType, String[] forbiddenValue, String occurrence) {
		if (forbiddenType == Forbidden.Type.FIXED) {
			return Arrays.asList(forbiddenValue).contains(occurrence);
		} else if (forbiddenType == Forbidden.Type.PREFIX) {
			return Arrays.stream(forbiddenValue).anyMatch(occurrence::startsWith);
		} else if (forbiddenType == Forbidden.Type.WILDCARD) {
			return Arrays.stream(forbiddenValue) //
					.map(s -> s.replace(".", "\\.") //
							.replace("$", "\\$") //
							.replaceAll("(?<!\\*)\\*(?!\\*)", "[^.\\$]*") //
							.replace("**", ".*")) //
					.anyMatch(regex -> Pattern.matches(regex, occurrence));
		}
		System.out.println("unsupported type for @Forbidden or @NotForbidden");
		System.exit(-4);
		return false;
	}

	private static boolean isForbidden(TestAnnotations testAnnotations, String usedName) {
		return ( //
				((usedName.startsWith("java.") || usedName.startsWith("javax.") || usedName.startsWith("jdk.") || usedName.startsWith("com.") || usedName.startsWith("org.")) //
						&& !matchesForbiddenOrNotForbidden(Forbidden.Type.WILDCARD, DEFAULT_NOT_FORBIDDEN, usedName)) //
						|| testAnnotations.forbidden.stream().anyMatch(f -> matchesForbiddenOrNotForbidden(f.type(), f.value(), usedName))) //
				&& testAnnotations.notForbidden.stream().noneMatch(f -> matchesForbiddenOrNotForbidden(f.type(), f.value(), usedName));
	}
}