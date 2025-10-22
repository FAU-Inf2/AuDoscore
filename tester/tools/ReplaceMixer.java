package tester.tools;

import java.lang.classfile.*;
import java.lang.classfile.attribute.*;
import java.lang.classfile.constantpool.*;
import java.lang.classfile.instruction.*;
import java.lang.constant.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class ReplaceMixer {
	private static String getMethodSignature(MethodModel mm) {
		// to cope with boxing/unboxing, virtually unify signatures to primitive types only:
		String mtd = mm.methodType().stringValue();
		mtd = mtd.replace("Ljava/lang/Boolean;", "Z") //
				.replace("Ljava/lang/Byte;", "B") //
				.replace("Ljava/lang/Character;", "C") //
				.replace("Ljava/lang/Short;", "S") //
				.replace("Ljava/lang/Integer;", "I") //
				.replace("Ljava/lang/Long;", "J") //
				.replace("Ljava/lang/Float;", "F") //
				.replace("Ljava/lang/Double;", "D");
		return mm.parent().map(cmP -> cmP.thisClass().asInternalName()).orElse("<anon>") + "." + mm.methodName().stringValue() + ":" + mtd;
	}

	public static void replace(String classToReplace, SortedSet<String> methodsToReplace, Path cleanroomPath, Path studentPath, Path mixedPath) {
		try (var cleanroomFiles = Files.walk(cleanroomPath, 2); var studentFiles = java.nio.file.Files.walk(studentPath, 2)) {
			final Set<FieldModel> cleanroomFields = new HashSet<>();
			final List<CodeElement> cleanroomFieldsInitStatic = new ArrayList<>();
			final List<CodeElement> cleanroomFieldsInitNonStatic = new ArrayList<>();
			final Map<String, MethodModel> cleanroomMethods = new HashMap<>();
			final Set<MethodModel> cleanroomMethodsLambdas = new HashSet<>();
			var cleanroomClassFiles = cleanroomFiles.filter(p -> p.toFile().isFile() && p.getFileName().toString().equals(classToReplace + ".class")).toList();
			for (Path cleanroomClassFile : cleanroomClassFiles) {
				final ClassModel cm = ClassFile.of().parse(Files.readAllBytes(cleanroomClassFile));
				for (FieldModel fm : cm.fields()) {
					if (fm.fieldName().stringValue().startsWith("__clean")) {
						// extract "__clean..."-fields from cleanroom:
						cleanroomFields.add(fm);
					}
				}
				for (MethodModel mm : cm.methods()) {
					if (methodsToReplace.contains(mm.methodName().stringValue())) {
						// extract declared replacement method from cleanroom:
						cleanroomMethods.put(getMethodSignature(mm), mm);
					} else if (mm.methodName().stringValue().startsWith("lambda$") // method originates from anonymous lambda
							&& methodsToReplace.contains(mm.methodName().stringValue().split("\\$")[1])) { // byte code: "lambda$<originalMethodName>$<numId>"
						// extract lambda methods originating from declared replacement method:
						cleanroomMethodsLambdas.add(mm);
					} else if (mm.methodName().equalsString(ConstantDescs.CLASS_INIT_NAME) || mm.methodName().equalsString(ConstantDescs.INIT_NAME)) {
						// extract init code of "__clean..."-fields from cleanroom:
						final Set<Utf8Entry> cleanroomFields_names = cleanroomFields.stream().map(FieldModel::fieldName).collect(Collectors.toSet());
						List<CodeElement> codeElements = mm.code().map(CompoundElement::elementList).orElse(new ArrayList<>());
						for (int pc = 0; pc < codeElements.size(); pc++) {
							CodeElement codeElement = codeElements.get(pc);
							if (codeElement instanceof FieldInstruction fieldInstruction //
									&& (fieldInstruction.opcode().equals(Opcode.PUTSTATIC) || fieldInstruction.opcode().equals(Opcode.PUTFIELD)) //
									&& cleanroomFields_names.contains(fieldInstruction.name())) {
								for (int pcBack = pc; pcBack >= 0 && !(codeElements.get(pcBack) instanceof LineNumber); pcBack--) {
									if (fieldInstruction.opcode().equals(Opcode.PUTSTATIC)) {
										cleanroomFieldsInitStatic.addFirst(codeElements.get(pcBack));
									} else {
										cleanroomFieldsInitNonStatic.addFirst(codeElements.get(pcBack));
									}
								}
							}
						}
					}
				}
			}
			var studentClassFiles = studentFiles.filter(p -> p.toFile().isFile() && p.getFileName().toString().equals(classToReplace + ".class")).toList();
			for (Path studentClassFile : studentClassFiles) {
				ClassModel cm = ClassFile.of().parse(Files.readAllBytes(studentClassFile));
				var mixedFile = FileSystems.getDefault().getPath(mixedPath.toString(), studentClassFile.toFile().getName());
				ClassFile.of().buildTo(mixedFile, cm.thisClass().asSymbol(), classBuilder -> {
					List<CodeElement> initCodeStatic = List.of(ReturnInstruction.of(Opcode.RETURN)); // if no class init code exists -> just put a return
					for (FieldModel fm : cleanroomFields) {
						classBuilder.transformField(fm, FieldTransform.ACCEPT_ALL);
					}
					for (ClassElement ce : cm) {
						switch (ce) {
							case MethodModel mm when mm.methodName().equalsString(ConstantDescs.CLASS_INIT_NAME) -> // save existing class init code from student and remove it
									initCodeStatic = mm.code().map(CompoundElement::elementList).orElse(List.of(ReturnInstruction.of(Opcode.RETURN)));
							case MethodModel mm when mm.methodName().equalsString(ConstantDescs.INIT_NAME) -> {
								final List<CodeElement> initCodeNonStatic = new ArrayList<>(mm.code().map(CompoundElement::elementList).orElse(new ArrayList<>()));
								classBuilder.withMethodBody(mm.methodName(), mm.methodType(), mm.flags().flagsMask(), codeBuilder -> {
									// take over everything until super()-call from original cons:
									while (!initCodeNonStatic.isEmpty()) {
										CodeElement instruction = initCodeNonStatic.removeFirst();
										codeBuilder.with(instruction);
										if (instruction instanceof InvokeInstruction invokeInstruction && invokeInstruction.opcode().equals(Opcode.INVOKESPECIAL)) {
											break;
										}
									}
									// mix in collected init instructions
									for (CodeElement codeElement : cleanroomFieldsInitNonStatic) {
										codeBuilder.with(codeElement);
									}
									// take over rest from original cons
									for (CodeElement codeElement : initCodeNonStatic) {
										codeBuilder.with(codeElement);
									}
								});
							}
							case MethodModel mm when mm.methodName().stringValue().startsWith("lambda$") //
									&& methodsToReplace.contains(mm.methodName().stringValue().split("\\$")[1]) -> {
								// skip lambda method from student code if it belongs to a replaced method
							}
							case MethodModel mm -> {
								MethodModel mmReplacement = cleanroomMethods.getOrDefault(getMethodSignature(mm), mm);
								if (mmReplacement != mm) {
									// to cope with different generic type parameters: remove generic signature of cleanroom method
									classBuilder.transformMethod(mmReplacement, MethodTransform.dropping(me -> me instanceof SignatureAttribute));
								} else {
									classBuilder.with(mm);
								}
							}
							default -> classBuilder.with(ce);
						}
					}
					// create static init method; mix code from cleanroom and from student:
					final List<CodeElement> finalStaticInitCode = initCodeStatic;
					classBuilder.withMethodBody(ConstantDescs.CLASS_INIT_NAME, MethodTypeDesc.of(ConstantDescs.CD_void), ClassFile.ACC_STATIC, codeBuilder -> {
						for (CodeElement codeElement : cleanroomFieldsInitStatic) {
							codeBuilder.with(codeElement);
						}
						for (CodeElement codeElement : finalStaticInitCode) {
							codeBuilder.with(codeElement);
						}
					});
					// add cleanroom lambda methods for replaced cleanroom methods:
					for (MethodModel lambdaMM : cleanroomMethodsLambdas) {
						classBuilder.with(lambdaMM);
					}
				});
			}
		} catch (java.io.IOException e) {
			System.err.println("Failed!");
			e.printStackTrace(System.err);
		}
	}
}
