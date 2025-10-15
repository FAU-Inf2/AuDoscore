package tools;

import java.lang.classfile.*;
import java.lang.classfile.instruction.*;
import java.lang.constant.*;
import java.nio.file.*;
import java.util.*;

public class ReplaceMixer {
	private static String getMethodSignature(MethodModel mm) {
		return mm.parent().map(cmP -> cmP.thisClass().asInternalName()).orElse("<anon>") + "." + mm.methodName().stringValue() //
				+ ":" + mm.methodTypeSymbol().displayDescriptor();
	}

	public static void replace(String classToReplace, SortedSet<String> methodsToReplace, Path cleanroomPath, Path studentPath, Path mixedPath) {
		try (var cleanroomFiles = Files.walk(cleanroomPath, 2); var studentFiles = java.nio.file.Files.walk(studentPath, 2)) {
			final HashSet<FieldModel> cleanroomFields = new HashSet<>();
			final HashSet<ClassDesc> cleanroomFields_fieldTypeSymbols = new HashSet<>();
			final List<CodeElement> cleanroomFieldsInitStatic = new ArrayList<>();
			final List<CodeElement> cleanroomFieldsInitNonStatic = new ArrayList<>();
			final HashMap<String, MethodModel> cleanroomMethods = new HashMap<>();
			var cleanroomClassFiles = cleanroomFiles.filter(p -> p.toFile().isFile() && p.getFileName().toString().equals(classToReplace + ".class")).toList();
			for (Path cleanroomClassFile : cleanroomClassFiles) {
				ClassModel cm = ClassFile.of().parse(Files.readAllBytes(cleanroomClassFile));
				for (FieldModel fm : cm.fields()) {
					if (fm.fieldName().stringValue().startsWith("__clean")) {
						// extract "__clean..."-fields from cleanroom:
						cleanroomFields.add(fm);
						cleanroomFields_fieldTypeSymbols.add(fm.fieldTypeSymbol());
					}
				}
				for (MethodModel mm : cm.methods()) {
					if (methodsToReplace.contains(mm.methodName().stringValue())) {
						// extract replacement method from cleanroom:
						cleanroomMethods.put(getMethodSignature(mm), mm);
					} else if (mm.methodName().equalsString(ConstantDescs.CLASS_INIT_NAME) || mm.methodName().equalsString(ConstantDescs.INIT_NAME)) {
						// extract init code of "__clean..."-fields from cleanroom:
						List<CodeElement> codeElements = mm.code().map(CompoundElement::elementList).orElse(new ArrayList<>());
						for (int pc = 0; pc < codeElements.size(); pc++) {
							CodeElement codeElement = codeElements.get(pc);
							if (codeElement instanceof FieldInstruction fieldInstruction && (fieldInstruction.opcode().equals(Opcode.PUTSTATIC) || fieldInstruction.opcode().equals(Opcode.PUTFIELD))) {
								if (cleanroomFields_fieldTypeSymbols.contains(fieldInstruction.typeSymbol())) {
									for (int pcBack = pc; pcBack >= 0 && !(codeElements.get(pcBack) instanceof LineNumber); pcBack--) {
										if (fieldInstruction.opcode().equals(Opcode.PUTSTATIC)) {
											cleanroomFieldsInitStatic.addFirst(codeElements.get(pcBack));
										} else {
											cleanroomFieldsInitNonStatic.addFirst(codeElements.get(pcBack));
										}
									}
									cleanroomFields_fieldTypeSymbols.remove(fieldInstruction.typeSymbol());
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
					List<CodeElement> initCodeStatic = List.of(ReturnInstruction.of(Opcode.RETURN));
					for (ClassElement ce : cleanroomFields) {
						classBuilder.with(ce);
					}
					for (ClassElement ce : cm) {
						switch (ce) {
							case MethodModel mm when mm.methodName().equalsString(ConstantDescs.CLASS_INIT_NAME) -> //
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
							case MethodModel mm -> {
								// TODO: cope with boxing/unboxing:
								mm = cleanroomMethods.getOrDefault(getMethodSignature(mm), mm);
								classBuilder.with(mm);
							}
							default -> classBuilder.with(ce);
						}
					}
					final List<CodeElement> finalStaticInitCode = initCodeStatic;
					classBuilder.withMethodBody(ConstantDescs.CLASS_INIT_NAME, MethodTypeDesc.of(ConstantDescs.CD_void), ClassFile.ACC_STATIC, codeBuilder -> {
						for (CodeElement codeElement : cleanroomFieldsInitStatic) {
							codeBuilder.with(codeElement);
						}
						for (CodeElement codeElement : finalStaticInitCode) {
							codeBuilder.with(codeElement);
						}
					});
				});
			}
		} catch (java.io.IOException e) {
			System.err.println("Failed!");
			e.printStackTrace(System.err);
		}
	}
}
