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
			"java.lang.Object**", "java.lang.(Boolean**|Byte**|Character**|Double**|Float**|Integer**|Long**|Short**)", // wrapper
			"java.lang.(String|StringBuffer|StringBuilder).**", //
			"java.lang.(Enum|Record).**", //
			"java.lang.(Math|StrictMath).**", //
			"java.lang.(IllegalArgumentException|IndexOutOfBoundsException|NullPointerException|NumberFormatException|Throwable).**", //
			"java.lang.ScopedValue.**", //
			"java.lang.System.(arraycopy|currentTimeMillis|nanoTime).**", //
			"java.lang.(Thread|ThreadLocal).**", //
			"java.io.PrintStream.print*", // for System.[out|err].println
			"java.io.(StringReader|StringWriter).**", //
			"java.math.**", //
			"java.util.**", //
	};

	public static void search(String rootDir, String pubTestName) {
		var testAnnotations = getTestAnnotations(rootDir, pubTestName);
		var studentClassDir = FileSystems.getDefault().getPath(rootDir, "student");
		try (var studentClassFiles = Files.walk(studentClassDir, 1).filter(f -> f.toString().endsWith(".class"))) {
			List<Finding> findings = new LinkedList<>();
			for (Path studentClassFile : studentClassFiles.toList()) {
				ClassModel cm = ClassFile.of().parse(Files.readAllBytes(studentClassFile));
				int lineNumber = 0;
				for (MethodModel mm : cm.methods()) {
					for (CodeElement codeElement : mm.code().map(CompoundElement::elementList).orElse(List.of())) {
						if (codeElement instanceof LineNumber ln) {
							lineNumber = ln.line();
						} else if (codeElement instanceof InvokeInstruction i) {
							String usedName = i.owner().asInternalName().replace('/', '.') + "." + i.name().stringValue() + "(" + i.type().stringValue() + ")";
							if ((usedName.startsWith("java.") || usedName.startsWith("jdk.")) && isForbidden(testAnnotations, usedName)) {
								findings.add(new Finding(studentClassFile, lineNumber, usedName));
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

	private record Finding(Path studentClassFile, int lineNumber, String usedName) {
		@Override
		public String toString() {
			return "# " + studentClassFile.getFileName().toString() + ":" + lineNumber + ": " + usedName;
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
		return (!matchesForbiddenOrNotForbidden(Forbidden.Type.WILDCARD, DEFAULT_NOT_FORBIDDEN, usedName) //
				|| testAnnotations.forbidden.stream().anyMatch(f -> matchesForbiddenOrNotForbidden(f.type(), f.value(), usedName))) //
				&& testAnnotations.notForbidden.stream().noneMatch(f -> matchesForbiddenOrNotForbidden(f.type(), f.value(), usedName));
	}
}