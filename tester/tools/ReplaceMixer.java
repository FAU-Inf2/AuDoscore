package tester.tools;

import java.lang.classfile.*;
import java.lang.classfile.attribute.*;
import java.lang.classfile.constantpool.*;
import java.lang.classfile.instruction.*;
import java.lang.constant.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class ReplaceMixer {
	public static void replace(String classToReplace, SortedSet<String> methodsToReplace, Path cleanroomPath, Path studentPath, Path mixedPath) {
		try (var cleanroomClassFilesStream = Files.find(cleanroomPath, 42, (p, a) -> a.isRegularFile() && p.toString().endsWith(".class")); //
			 var studentClassFilesStream = Files.find(studentPath, 42, (p, a) -> a.isRegularFile() && p.toString().endsWith(".class"))) {
			var cleanroomClassesObfuscated = new HashMap<Path, ByteArrayOutputStream>();
			var cleanroomFields = new HashSet<FieldModel>();
			var cleanroomFieldsInitStatic = new ArrayList<CodeElement>();
			var cleanroomFieldsInitNonStatic = new ArrayList<CodeElement>();
			var cleanroomMethods = new HashMap<String, MethodModel>();
			var cleanroomMethodsLambdas = new HashSet<MethodModel>();
			var cleanroomInnerClassesNames = new HashSet<String>();
			// ========== process cleanroom classes first: ==========
			var cleanroomClassFiles = cleanroomClassFilesStream.toList();
			var cleanroomClassFilesToReplace = cleanroomClassFiles.stream().filter(p -> p.getFileName().toString().equals(classToReplace + ".class")).toList();
			// collect all cleanroom inner class names:
			for (Path cleanroomClassFile : cleanroomClassFiles) {
				var classModel = ClassFile.of().parse(cleanroomClassFile);
				classModel.elementStream().filter(ce -> ce instanceof InnerClassesAttribute).map(ce -> (InnerClassesAttribute) ce) //
						.flatMap(ica -> ica.classes().stream()) //
						.filter(ici -> classModel.thisClass().equals(ici.outerClass().orElse(classModel.thisClass()))) // only if inner class of current class
						.map(InnerClassInfo::innerClass).map(ClassEntry::name).map(Utf8Entry::stringValue) //
						.forEach(cleanroomInnerClassesNames::add);
			}
			// "obfuscate" (i.e. rename) inner classes of all cleanroom code:
			for (Path cleanroomClassFile : cleanroomClassFiles) {
				var cleanroomClassName = ClassFile.of().parse(cleanroomClassFile).thisClass().name().stringValue();
				var cleanroomClassObfuscatedBytes = new ByteArrayOutputStream();
				cleanroomClassesObfuscated.put(cleanroomClassFile, cleanroomClassObfuscatedBytes);
				try (var inFile = new DataInputStream(new FileInputStream(cleanroomClassFile.toFile())); var outByteArray = new DataOutputStream(cleanroomClassObfuscatedBytes)) {
					InnerClassRenamer.obfuscateByteCode(cleanroomInnerClassesNames, inFile, outByteArray);
					if (cleanroomInnerClassesNames.contains(cleanroomClassName)) { // emit ONLY inner classes to mixed folder:
						var newCleanroomClassName = InnerClassRenamer.obfuscateString(cleanroomInnerClassesNames, cleanroomClassFile.toFile().getName());
						var mixedFile = FileSystems.getDefault().getPath(mixedPath.toString(), newCleanroomClassName);
						try (var outFile = new FileOutputStream(mixedFile.toFile())) {
							outFile.write(cleanroomClassObfuscatedBytes.toByteArray());
						}
					}
				}
			}
			// extract fields, methods and (static) init code from cleanroom:
			for (Path cleanroomClassFileToReplace : cleanroomClassFilesToReplace) {
				var classModel = ClassFile.of().parse(cleanroomClassesObfuscated.get(cleanroomClassFileToReplace).toByteArray());
				// extract replacement methods:
				classModel.methods().stream().filter(mm -> methodsToReplace.contains(mm.methodName().stringValue())).forEach(mm -> cleanroomMethods.put(getMethodSignature(mm), mm));
				// extract lambdas (synthetic lambda methods) originating from replacement methods:
				classModel.methods().stream().filter(mm -> mm.methodName().stringValue().startsWith("lambda$") // method originates from anonymous lambda
								&& methodsToReplace.contains(mm.methodName().stringValue().split("\\$")[1])) // byte code: "lambda$<originalMethodName>$<numId>"
						.forEach(cleanroomMethodsLambdas::add);
				// extract "__clean..."-fields:
				classModel.fields().stream().filter(fm -> fm.fieldName().stringValue().startsWith("__clean")).forEach(cleanroomFields::add);
				// extract (static) init code of "__clean..."-fields:
				for (MethodModel mm : classModel.methods()) {
					if (mm.methodName().equalsString(ConstantDescs.CLASS_INIT_NAME) || mm.methodName().equalsString(ConstantDescs.INIT_NAME)) {
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
			// ========== process student classes last: ==========
			var studentClassFilesToReplace = studentClassFilesStream.filter(p -> p.toFile().isFile() && p.getFileName().toString().equals(classToReplace + ".class")).toList();
			for (Path studentClassFileToReplace : studentClassFilesToReplace) {
				var cm = ClassFile.of().parse(studentClassFileToReplace);
				var mixedFile = FileSystems.getDefault().getPath(mixedPath.toString(), studentClassFileToReplace.toFile().getName());
				var studentInitStatic = new LinkedList<CodeElement>();
				ClassFile.of().buildTo(mixedFile, cm.thisClass().asSymbol(), classBuilder -> {
					cleanroomFields.forEach(fm -> classBuilder.transformField(fm, FieldTransform.ACCEPT_ALL)); // mix in "__clean..."-fields
					for (ClassElement ce : cm) { // now process the entire student class
						switch (ce) {
							case MethodModel mm when mm.methodName().equalsString(ConstantDescs.CLASS_INIT_NAME) -> // save existing class init code from student and skip it for now
									studentInitStatic.addAll(mm.code().map(CompoundElement::elementList).orElse(List.of()));
							case MethodModel mm when mm.methodName().equalsString(ConstantDescs.INIT_NAME) -> { // rewrite instance init code from student mixing in cleanroom field init
								var studentInitNonStatic = new ArrayList<>(mm.code().map(CompoundElement::elementList).orElse(new ArrayList<>()));
								classBuilder.transformMethod(mm, MethodTransform.transformingCode(new CodeTransform() {
									@Override
									public void atStart(CodeBuilder codeBuilder) { // take over everything until super()-call from original cons:
										CodeTransform.super.atStart(codeBuilder);
										while (!studentInitNonStatic.isEmpty()) {
											CodeElement instruction = studentInitNonStatic.removeFirst();
											codeBuilder.accept(instruction);
											if (instruction instanceof InvokeInstruction ii && ii.opcode().equals(Opcode.INVOKESPECIAL)) break;
										}
										cleanroomFieldsInitNonStatic.forEach(codeBuilder); // now mix in collected init instructions from cleanroom cons
										studentInitNonStatic.forEach(codeBuilder); // finally take over rest from original student cons
									}

									@Override
									public void accept(CodeBuilder builder, CodeElement element) { // do not process student code here as we already did above
									}
								}));
							}
							case MethodModel mm when mm.methodName().stringValue().startsWith("lambda$") //
									&& methodsToReplace.contains(mm.methodName().stringValue().split("\\$")[1]) -> {
								// skip lambda method from student code if it belongs to a replaced method
							}
							case MethodModel mm -> classBuilder.transformMethod(cleanroomMethods.getOrDefault(getMethodSignature(mm), mm), MethodTransform.ACCEPT_ALL // replace?
									.andThen(MethodTransform.endHandler(mb -> mm.findAttribute(Attributes.signature()).ifPresent(mb)))); // but keep signature!
							default -> classBuilder.accept(ce);
						}
					}
					// if necessary: (re)create static init method, mixing code from cleanroom and from student:
					if (!cleanroomFieldsInitStatic.isEmpty() || !studentInitStatic.isEmpty()) {
						classBuilder.withMethodBody(ConstantDescs.CLASS_INIT_NAME, MethodTypeDesc.of(ConstantDescs.CD_void), ClassFile.ACC_STATIC, codeBuilder -> {
							cleanroomFieldsInitStatic.forEach(codeBuilder);
							studentInitStatic.forEach(codeBuilder);
							if (studentInitStatic.isEmpty()) codeBuilder.with(ReturnInstruction.of(Opcode.RETURN)); // if there was no student class init, add "return" now!
						});
					}
					cleanroomMethodsLambdas.forEach(classBuilder); // add cleanroom lambda methods for replaced cleanroom methods
				});
			}
		} catch (java.io.IOException e) {
			System.err.println("Failed!");
			e.printStackTrace(System.err);
		}
	}

	private static class InnerClassRenamer {
		private static String obfuscateString(Set<String> cleanroomInnerClassNames, String name) {
			String newName = name;
			if (cleanroomInnerClassNames.contains(name)) { // it's a plain type
				newName = name + AUDOSCORE_SECURITY_TOKEN;
			} else if (name.endsWith(".class")) { // it's a class file name
				String fileName = name.substring(0, name.length() - 6);
				if (cleanroomInnerClassNames.contains(fileName)) {
					newName = fileName + AUDOSCORE_SECURITY_TOKEN + ".class";
				}
			} else { // it might be a descriptor string with several different occurrences
				for (String key : cleanroomInnerClassNames) {
					String descriptorString = "L" + key + ";";
					if (name.contains(descriptorString)) {
						newName = newName.replace(descriptorString, "L" + key + AUDOSCORE_SECURITY_TOKEN + ";");
					}
				}
			}
			return newName;
		}

		private static void obfuscateByteCode(Set<String> cleanroomInnerClassNames, DataInputStream in, DataOutputStream out) throws IOException {
			// https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.1
			int magic = in.readInt(); // u4 magic;
			if (magic != 0xCAFEBABE) throw new ClassFormatError("wrong magic number: " + magic);
			out.writeInt(magic);
			copy(in, out, 4); // u2 minor_version; u2 major_version;
			int constant_pool_count = in.readUnsignedShort(); // u2 constant_pool_count;
			out.writeShort(constant_pool_count);
			for (int i = 1; i < constant_pool_count; i++) { // cp_info constant_pool[constant_pool_count-1];
				int tag = in.readUnsignedByte(); // u1 tag;
				out.writeByte(tag);
				ConstantPoolEntry cpe = ConstantPoolEntry.getConstantPoolEntry(tag);
				if (cpe == ConstantPoolEntry.CONSTANT_Utf8) {
					out.writeUTF(obfuscateString(cleanroomInnerClassNames, in.readUTF()));
				} else {
					copy(in, out, cpe.size);
				}
				i += (cpe.slots - 1);
			}
			// copy rest of class file without modifications:
			in.transferTo(out);
		}

		// if running to verify provided tests, a constant token is exported by the verify script for regression comparison:
		private static final String AUDOSCORE_SECURITY_TOKEN = "_" + System.getenv().getOrDefault("AUDOSCORE_SECURITY_TOKEN", generateRandomToken());

		private static String generateRandomToken() {
			Random r = new Random();
			StringBuilder s = new StringBuilder();
			for (int i = 0; i < 32; i++) {
				s.append((char) ('A' + r.nextInt('Z' - 'A' + 1)));
			}
			return s.toString();
		}

		private static final byte[] buffer = new byte[8]; // largest pool entries with constant width have at most 8 bytes

		private static void copy(DataInputStream in, DataOutputStream out, int amount) throws IOException {
			in.readFully(buffer, 0, amount);
			out.write(buffer, 0, amount);
		}

		private enum ConstantPoolEntry { // https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4-210
			CONSTANT_Utf8(PoolEntry.TAG_UTF8, -1, 1), // u1 tag; u2 length; u1 bytes[length];
			CONSTANT_Integer(PoolEntry.TAG_INTEGER, 4, 1), // u1 tag; u4 bytes;
			CONSTANT_Float(PoolEntry.TAG_FLOAT, 4, 1), // u1 tag; u4 bytes;
			CONSTANT_Long(PoolEntry.TAG_LONG, 8, 2), // u1 tag; u4 high_bytes; u4 low_bytes;
			CONSTANT_Double(PoolEntry.TAG_DOUBLE, 8, 2), // u1 tag; u4 high_bytes; u4 low_bytes;
			CONSTANT_Class(PoolEntry.TAG_CLASS, 2, 1), // u1 tag; u2 name_index;
			CONSTANT_String(PoolEntry.TAG_STRING, 2, 1), // u1 tag; u2 string_index;
			CONSTANT_FieldRef(PoolEntry.TAG_FIELDREF, 4, 1), // u1 tag; u2 class_index; u2 name_and_type_index;
			CONSTANT_MethodRef(PoolEntry.TAG_METHODREF, 4, 1), // u1 tag; u2 class_index; u2 name_and_type_index;
			CONSTANT_InterfaceMethodRef(PoolEntry.TAG_INTERFACE_METHODREF, 4, 1), //u1 tag; u2 class_index; u2 name_and_type_index;
			CONSTANT_NameAndType(PoolEntry.TAG_NAME_AND_TYPE, 4, 1), // u1 tag; u2 name_index; u2 descriptor_index;
			CONSTANT_MethodHandle(PoolEntry.TAG_METHOD_HANDLE, 3, 1), // u1 tag; u1 reference_kind; u2 reference_index;
			CONSTANT_MethodType(PoolEntry.TAG_METHOD_TYPE, 2, 1), // u1 tag; u2 descriptor_index;
			CONSTANT_Dynamic(PoolEntry.TAG_DYNAMIC, 4, 1), // u1 tag; u2 bootstrap_method_attr_index; u2 name_and_type_index;
			CONSTANT_InvokeDynamic(PoolEntry.TAG_INVOKE_DYNAMIC, 4, 1), // u1 tag; u2 bootstrap_method_attr_index; u2 name_and_type_index;
			CONSTANT_Module(PoolEntry.TAG_MODULE, 2, 1), // u1 tag; u2 name_index;
			CONSTANT_Package(PoolEntry.TAG_PACKAGE, 2, 1); // u1 tag; u2 name_index;

			final int tag, size, slots;
			// @slots: https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.5
			// "In retrospect, making 8-byte constants take two constant pool entries was a poor choice."

			ConstantPoolEntry(int tag, int size, int slots) {
				this.tag = tag;
				this.size = size;
				this.slots = slots;
			}

			private static ConstantPoolEntry getConstantPoolEntry(int tag) {
				ConstantPoolEntry cpe = Arrays.stream(ConstantPoolEntry.values()).filter(e -> e.tag == tag).findFirst().orElse(null);
				if (cpe == null) throw new ClassFormatError("Unknown constant pool tag in class file: " + tag);
				return cpe;
			}
		}
	}

	private static String getMethodSignature(MethodModel mm) {
		// to cope with boxing/unboxing, virtually unify signatures to primitive types only:
		String mtd = mm.methodTypeSymbol().descriptorString();
		mtd = mtd.replace(ConstantDescs.CD_Boolean.descriptorString(), ConstantDescs.CD_boolean.descriptorString()) //
				.replace(ConstantDescs.CD_Byte.descriptorString(), ConstantDescs.CD_byte.descriptorString()) //
				.replace(ConstantDescs.CD_Character.descriptorString(), ConstantDescs.CD_char.descriptorString()) //
				.replace(ConstantDescs.CD_Short.descriptorString(), ConstantDescs.CD_short.descriptorString()) //
				.replace(ConstantDescs.CD_Integer.descriptorString(), ConstantDescs.CD_int.descriptorString()) //
				.replace(ConstantDescs.CD_Long.descriptorString(), ConstantDescs.CD_long.descriptorString()) //
				.replace(ConstantDescs.CD_Float.descriptorString(), ConstantDescs.CD_float.descriptorString()) //
				.replace(ConstantDescs.CD_Double.descriptorString(), ConstantDescs.CD_double.descriptorString());
		return mm.parent().map(cmP -> cmP.thisClass().asInternalName()).orElse("<anon>") + "." + mm.methodName().stringValue() + ":" + mtd;
	}
}
