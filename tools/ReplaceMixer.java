package tools;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.regex.*;
import javax.annotation.processing.*;
import javax.lang.model.*;
import javax.lang.model.element.*;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.Tree.*;
import com.sun.source.util.*;
import com.sun.tools.javac.code.*;
import com.sun.tools.javac.tree.*;
import com.sun.tools.javac.tree.JCTree.*;

@SupportedAnnotationTypes("*")
@SupportedOptions("replaces")
@SupportedSourceVersion(SourceVersion.RELEASE_25)
public class ReplaceMixer extends AbstractProcessor {
	public static final String CLEAN_PREFIX = "__clean";
	private Trees trees;
	private final HashMap<String, Replacement> cleanMethods = new HashMap<>();
	private final HashMap<String, Replacement> cleanInnerClasses = new HashMap<>();
	private final HashMap<String, Replacement> cleanFields = new HashMap<>();
	private boolean isPublic = true;
	private final boolean[] imported = {false, false}; // cleanroom, student
	private boolean isCleanroom;
	private int classLevel;
	private String[] replaces = null;

	@Override
	public synchronized void init(ProcessingEnvironment env) {
		super.init(env);
		this.trees = Trees.instance(env);
		String repString = env.getOptions().get("replaces");
		if (repString != null) {
			this.replaces = repString.split("#");
		}
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (!roundEnv.processingOver()) {
			Set<? extends Element> elements = roundEnv.getRootElements();
			for (Element each : elements) {
				Element encl = each.getEnclosingElement();
				if (encl != null) {
					// check if inside package cleanroom
					isCleanroom = encl.getSimpleName().toString().equals("cleanroom");
				} else {
					isCleanroom = false;
				}
				if ((each.getKind() == ElementKind.CLASS) || (each.getKind() == ElementKind.INTERFACE)) {
					classLevel = 0;
					JCTree tree = (JCTree) trees.getTree(each);
					tree.accept(new Merger());
					TreePath path = trees.getPath(each);
					if (!imported[isCleanroom ? 0 : 1]) {
						imported[isCleanroom ? 0 : 1] = true;
						List<? extends ImportTree> imports = path.getCompilationUnit().getImports();
						for (Object o : imports) {
							System.out.print(o);
						}
					}
					if (!isCleanroom) {
						StringWriter stringWriter = new StringWriter();
						try {
							new tools.JavaSourcePrettyPrinter(stringWriter, false).printExpr(tree);
						} catch (IOException e) {
							throw new AssertionError(e);
						}
						System.out.println(stringWriter);
					}
				}
			}
			if (!isCleanroom) {
				// even with -proc:only the javac does some semantic checking
				// to avoid that (we compile the generated files anyway in the next step), exit in a clean way
				System.exit(0);
			}
		}
		return false;
	}

	private boolean isReplace(String method) {
		if (replaces != null) {
			for (String s : replaces) {
				if (method.startsWith(s + ":")) {
					return true;
				}
			}
		}
		return false;
	}

	private static class Replacement {
		final com.sun.tools.javac.util.List<JCTypeParameter> typeParams;
		final JCTree tree;

		Replacement(final com.sun.tools.javac.util.List<JCTypeParameter> typeParams, final JCTree tree) {
			this.typeParams = typeParams;
			this.tree = tree;
		}

		public boolean isStatic() {
			if (this.tree instanceof JCMethodDecl) {
				return ((JCMethodDecl) this.tree).getModifiers().getFlags().contains(Modifier.STATIC);
			} else if (this.tree instanceof JCVariableDecl) {
				return ((JCVariableDecl) this.tree).getModifiers().getFlags().contains(Modifier.STATIC);
			} else if (this.tree instanceof JCClassDecl) {
				return ((JCClassDecl) this.tree).getModifiers().getFlags().contains(Modifier.STATIC);
			}
			return false;
		}
	}

	private class Merger extends TreeTranslator {
		private final ArrayDeque<com.sun.tools.javac.util.List<JCTypeParameter>> typeParamStack = new ArrayDeque<>();
		private final ArrayDeque<JCClassDecl> classStack = new ArrayDeque<>();
		public boolean insideBlock = false;
		public boolean insideMethod = false;

		@Override
		public void visitBlock(JCBlock tree) {
			insideBlock = true;
			super.visitBlock(tree);
			insideBlock = false;
		}

		@Override
		public void visitVarDef(JCVariableDecl tree) {
			super.visitVarDef(tree);
			if (!insideBlock) {
				String name = tree.getName().toString();
				if (isCleanroom && name.startsWith(CLEAN_PREFIX)) {
					cleanFields.put(name, new Replacement(typeParamStack.peek(), tree));
				} else if (!isCleanroom && !insideMethod && tree.getModifiers().getFlags().contains(Modifier.FINAL) && tree.getInitializer() == null) {
					// in this case, there is an uninitialized final field in the student submission.
					// To avoid a compilation error if a constructor is replaced, we simply drop the 'final' modifier.
					tree.mods.flags &= ~com.sun.tools.javac.code.Flags.FINAL;
				}
			}
		}

		private String getTypesAsString(final ArrayList<String> types, final com.sun.tools.javac.util.List<JCTypeParameter> typeParameters) {
			final ArrayList<String> typesCopy = new ArrayList<>(types);
			for (int i = 0; i < typesCopy.size(); ++i) {
				String typeAsString = typesCopy.get(i);
				for (int j = 0; j < typeParameters.size(); ++j) {
					typeAsString = typeAsString.replaceAll("\\b" + typeParameters.get(j).getName().toString() + "\\b", "§_typeParam_" + j);
				}
				typesCopy.set(i, typeAsString);
			}
			return Arrays.toString(typesCopy.toArray());
		}

		private ArrayList<String> getTypes(final JCMethodDecl tree) {
			ArrayList<String> types = new ArrayList<>();
			for (JCVariableDecl variableDeclaration : tree.getParameters()) {
				final Symbol paramTypeSymbol = TreeInfo.symbol(variableDeclaration.getType());
				String fullyQualifiedType;
				if (paramTypeSymbol != null) {
					Type paramType = paramTypeSymbol.asType();
					if (paramType instanceof Type.TypeVar) {
						paramType = paramType.getUpperBound();
						if (paramType instanceof Type.ClassType && paramType.isCompound()) {
							paramType = ((Type.ClassType) paramType).supertype_field;
						}
					}
					if (paramType instanceof final Type.ClassType classType) {
						if (classType.getTypeArguments().nonEmpty()) {
							paramType = classType.asElement().erasure_field;
						}
					}
					if (paramType == null) {
						fullyQualifiedType = paramTypeSymbol.toString();
					} else {
						fullyQualifiedType = paramType.toString();
					}
					if (fullyQualifiedType.indexOf("cleanroom.") == 0) {
						fullyQualifiedType = fullyQualifiedType.substring("cleanroom.".length());
					}
				} else {
					fullyQualifiedType = variableDeclaration.getType().toString();
				}
				types.add(fullyQualifiedType);
			}
			return types;
		}

		private Pattern getBoxingAwareMethodNamePattern(final String methodName) {
			final String escapedMethodName = methodName.replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]");
			String methodPattern = escapedMethodName;
			methodPattern = methodPattern.replaceAll("\\b((java\\.lang\\.)?B|b)oolean\\b", "((java\\.lang\\.)?B|b)oolean");
			methodPattern = methodPattern.replaceAll("\\b((java\\.lang\\.)?B|b)yte\\b", "((java\\.lang\\.)?B|b)yte");
			methodPattern = methodPattern.replaceAll("\\b((java\\.lang\\.)?S|s)hort\\b", "((java\\.lang\\.)?S|s)hort");
			methodPattern = methodPattern.replaceAll("\\b((java\\.lang\\.)?Character|char)\\b", "((java\\.lang\\.)?Character|char)");
			methodPattern = methodPattern.replaceAll("\\b((java\\.lang\\.)?Integer|int)\\b", "((java\\.lang\\.)?Integer|int)");
			methodPattern = methodPattern.replaceAll("\\b((java\\.lang\\.)?L|l)ong\\b", "((java\\.lang\\.)?L|l)ong");
			methodPattern = methodPattern.replaceAll("\\b((java\\.lang\\.)?F|f)loat\\b", "((java\\.lang\\.)?F|f)loat");
			methodPattern = methodPattern.replaceAll("\\b((java\\.lang\\.)?D|d)ouble\\b", "((java\\.lang\\.)?D|d)ouble");
			if (methodPattern.equals(escapedMethodName)) {
				return null;
			} else {
				return Pattern.compile(methodPattern);
			}
		}

		private Replacement matchMethod(final Map<String, Replacement> replacements, final String methodName, final boolean isStatic) {
			if (replacements.containsKey(methodName)) {
				// we use that method, so don't put it in later
				final Replacement replacement = replacements.remove(methodName);
				if (replacement.isStatic() == isStatic) {
					return replacement;
				} else {
					// one of the methods is static, but the other is not -> do not replace
					return null;
				}
			}
			final Pattern boxingAwareMethodNamePattern = getBoxingAwareMethodNamePattern(methodName);
			if (boxingAwareMethodNamePattern != null) {
				final Iterator<Map.Entry<String, Replacement>> iterator = replacements.entrySet().iterator();
				while (iterator.hasNext()) {
					final Map.Entry<String, Replacement> entry = iterator.next();
					if (boxingAwareMethodNamePattern.matcher(entry.getKey()).matches()) {
						// check that only one method matches
						int matchingCount = 0;
						assert this.classStack.peek() != null;
						for (final JCTree def : this.classStack.peek().getMembers()) {
							if (def instanceof final JCMethodDecl methodDeclaration) {
								final ArrayList<String> types = getTypes(methodDeclaration);
								final com.sun.tools.javac.util.List<JCTypeParameter> typeParams = typeParamStack.isEmpty() ? methodDeclaration.getTypeParameters() : typeParamStack.peek().appendList(methodDeclaration.getTypeParameters());
								final String name = methodDeclaration.getName().toString() + ": " + getTypesAsString(types, typeParams);
								if (boxingAwareMethodNamePattern.matcher(name).matches()) {
									matchingCount += 1;
								}
							}
						}
						if (matchingCount == 1) {
							// we use that method, so don't put it in later
							iterator.remove();
							return entry.getValue();
						} else {
							return null;
						}
					}
				}
			}
			return null;
		}

		@Override
		public void visitMethodDef(JCMethodDecl tree) {
			insideMethod = true;
			super.visitMethodDef(tree);
			insideMethod = false;
			if (classLevel != 1 || !isPublic) {
				return;
			}
			final ArrayList<String> types = getTypes(tree);
			final com.sun.tools.javac.util.List<JCTypeParameter> typeParams = typeParamStack.isEmpty() ? tree.getTypeParameters() : typeParamStack.peek().appendList(tree.getTypeParameters());
			String name = tree.getName().toString() + ": " + getTypesAsString(types, typeParams);
			if (isCleanroom) {
				cleanMethods.put(name, new Replacement(typeParamStack.peek(), tree));
			} else {
				final boolean isStatic = tree.getModifiers().getFlags().contains(Modifier.STATIC);
				final Replacement replacement = matchMethod(cleanMethods, name, isStatic);
				if (replacement != null) {
					if (isReplace(name)) {
						System.err.println("duplicate method: " + name + ", taken from cleanroom");

						result = new TypeParameterTranslator(replacement.typeParams, typeParams).translate(replacement.tree);
					} else {
						System.err.println("duplicate method: " + name + ", taken from student");
					}
				}
			}
		}

		private com.sun.tools.javac.util.List<JCTree> appendAll(com.sun.tools.javac.util.List<JCTree> list, final com.sun.tools.javac.util.List<JCTypeParameter> typeParams, final Map<String, Replacement> cleanObjects) {
			for (Map.Entry<String, Replacement> entry : cleanObjects.entrySet()) {
				final Replacement replacement = entry.getValue();
				list = list.append(new TypeParameterTranslator(replacement.typeParams, typeParams).translate(replacement.tree));
			}
			return list;
		}

		private boolean isAnonymousClassInClassStack() {
			for (final JCClassDecl tree : this.classStack) {
				if (tree.getSimpleName().isEmpty()) {
					// anonymous class found
					return true;
				}
			}
			return false;
		}

		@Override
		public void visitClassDef(JCClassDecl tree) {
			final boolean oldInsideMethod = insideMethod;
			insideMethod = false;
			this.classStack.push(tree);
			insideMethod = oldInsideMethod;
			JCModifiers mods = tree.getModifiers();
			boolean oldPublic = isPublic;
			isPublic = mods.getFlags().contains(Modifier.PUBLIC);
			System.err.println("class " + tree.getSimpleName() + " ispub " + isPublic);
			if (isCleanroom && !isAnonymousClassInClassStack() && !isPublic && !tree.getSimpleName().toString().startsWith(CLEAN_PREFIX)) {
				System.err.println("non-public class in cleanroom must be prefixed with " + CLEAN_PREFIX);
				System.exit(-1);
			}
			insideBlock = false;
			classLevel++;
			if (classLevel > 1) {
				System.err.println("found inner class: " + tree.getSimpleName());
			}
			if (tree.getKind() == Kind.ENUM) {
				// remove default constructor from *inner* enums to make code valid
				com.sun.tools.javac.util.List<JCTree> dragPointer = null;
				for (com.sun.tools.javac.util.List<JCTree> t = tree.getMembers(); t.nonEmpty(); t = t.tail) {
					if (t.head != null && t.head.getKind() == Kind.METHOD) {
						JCTree.JCMethodDecl m = (JCTree.JCMethodDecl) t.head;
						if (m.getName().toString().equals("<init>") && m.getParameters().size() == 0) {
							if (dragPointer == null) {
								tree.defs = t.tail;
							} else {
								dragPointer.tail = t.tail;
							}
							break;
						}
					}
					dragPointer = t;
				}
			}
			typeParamStack.push(tree.getTypeParameters());
			super.visitClassDef(tree);
			typeParamStack.pop();
			classLevel--;
			isPublic = oldPublic;
			insideMethod = oldInsideMethod;
			System.err.println("class " + tree.getSimpleName() + ", " + isCleanroom + ", " + classLevel + ", " + isPublic);
			if (isCleanroom && classLevel >= 1 && tree.getSimpleName().toString().startsWith(CLEAN_PREFIX)) {
				// remember additional inner classes of cleanroom
				// those will be added later in student's public class
				cleanInnerClasses.put(tree.getSimpleName().toString(), new Replacement(tree.getTypeParameters(), tree));
			}
			// only add methods, fields and inner classes in outer public class of student
			if (classLevel >= 1 // no outer class
					|| !mods.getFlags().contains(Modifier.PUBLIC) // no public class
					|| isCleanroom) { // no student class
				this.classStack.pop();
				return;
			}
			tree.defs = appendAll(tree.defs, tree.getTypeParameters(), cleanMethods);
			tree.defs = appendAll(tree.defs, tree.getTypeParameters(), cleanFields);
			tree.defs = appendAll(tree.defs, tree.getTypeParameters(), cleanInnerClasses);
			result = tree;
			this.classStack.pop();
		}
	}

	private static class TypeParameterTranslator extends TreeTranslator {
		private final Map<com.sun.tools.javac.util.Name, com.sun.tools.javac.util.Name> typeParamMap = new HashMap<>();
		private boolean inType = false;

		TypeParameterTranslator(final com.sun.tools.javac.util.List<JCTypeParameter> cleanroomTypeParams, final com.sun.tools.javac.util.List<JCTypeParameter> currentTypeParams) {
			if (cleanroomTypeParams != null && currentTypeParams != null) {
				for (final Iterator<JCTypeParameter> cleanroomIt = cleanroomTypeParams.iterator(), currentIt = currentTypeParams.iterator(); //
					 cleanroomIt.hasNext() && currentIt.hasNext(); ) {
					this.typeParamMap.put(cleanroomIt.next().getName(), currentIt.next().getName());
				}
			}
		}

		@Override
		public <T extends JCTree> T translate(final T tree) {
			if (inType && tree instanceof final JCIdent ident) {
				if (this.typeParamMap.containsKey(ident.name)) {
					ident.name = this.typeParamMap.get(ident.name);
				}
				return tree;
			}
			return super.translate(tree);
		}

		@Override
		public void visitMethodDef(JCMethodDecl tree) {
			tree.mods = translate(tree.mods);
			inType = true;
			tree.restype = translate(tree.restype);
			inType = false;
			tree.typarams = translateTypeParams(tree.typarams);
			tree.params = translateVarDefs(tree.params);
			inType = true;
			tree.thrown = translate(tree.thrown);
			inType = false;
			tree.body = translate(tree.body);
			result = tree;
		}

		@Override
		public void visitVarDef(final JCVariableDecl tree) {
			inType = true;
			tree.vartype = translate(tree.vartype);
			inType = false;
			tree.init = translate(tree.init);
			result = tree;
		}

		@Override
		public void visitTypeCast(final JCTypeCast tree) {
			inType = true;
			tree.clazz = translate(tree.clazz);
			inType = false;
			tree.expr = translate(tree.expr);
			result = tree;
		}

		@Override
		public void visitTypeParameter(final JCTypeParameter tree) {
			inType = true;
			super.visitTypeParameter(tree);
			inType = false;
		}

		@Override
		public void visitNewClass(JCNewClass tree) {
			tree.encl = translate(tree.encl);
			inType = true;
			for (com.sun.tools.javac.util.List<JCExpression> list = tree.typeargs; list.nonEmpty(); list = list.tail) {
				list.head = translate(list.head);
			}
			tree.clazz = translate(tree.clazz);
			inType = false;
			tree.args = translate(tree.args);
			tree.def = translate(tree.def);
			result = tree;
		}
	}
}
