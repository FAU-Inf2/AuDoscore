import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.*;
import javax.lang.model.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.ElementKind;
import javax.tools.*;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.Trees;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.*;
import com.sun.tools.javac.util.*;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.util.List;

@SupportedAnnotationTypes("*")
@SupportedOptions("replaces")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ReplaceMixer extends AbstractProcessor {
	public final String CLEAN_PREFIX = "__clean";
	private Trees trees;

	private HashMap<String, Replacement> cleanMethods = new HashMap<>();
	private HashMap<String, Replacement> cleanInnerClasses = new HashMap<>();
	private HashMap<String, Replacement> cleanFields = new HashMap<>();
	private boolean isPublic = true;
	private boolean imported[] = {false, false}; // cleanroom, student
	private boolean isCleanroom;
	private int classLevel;

	public String[] replaces = null;

	@Override
	public synchronized void init(ProcessingEnvironment env) {
		super.init(env);
		trees = Trees.instance(env);
		Context context = ((JavacProcessingEnvironment) env).getContext();
		String repString = env.getOptions().get("replaces");
		if (repString != null) {
			replaces = repString.split("#");
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
				if (each.getKind() == ElementKind.CLASS) {
					classLevel = 0;
					JCTree tree = (JCTree) trees.getTree(each);
					tree.accept(new Merger());

					TreePath path = trees.getPath(each);
					if (!imported[isCleanroom ? 0 : 1]) { 
						imported[isCleanroom ? 0 : 1] = true;
						java.util.List imports = path.getCompilationUnit().getImports();
						for (Object o : imports) {
							System.out.print(o);
						}
					}

					if (!isCleanroom) {
						System.out.println(tree);
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
		final List<JCTypeParameter> typeParams;
		final JCTree tree;

		Replacement(final List<JCTypeParameter> typeParams, final JCTree tree) {
			this.typeParams = typeParams;
			this.tree = tree;
		}
	}

	private class Merger extends TreeTranslator {

		private final ArrayDeque<List<JCTypeParameter>> typeParamStack = new ArrayDeque<>();

		public boolean insideBlock = false;


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
				String name = tree.name.toString();
				if (isCleanroom && name.startsWith(CLEAN_PREFIX)) {
					cleanFields.put(name, new Replacement(typeParamStack.peek(), tree));
				}
			}
		}

		private String getTypesAsString(final ArrayList<String> types,
				final List<JCTypeParameter> typeParameters) {
			
			final ArrayList<String> typesCopy = new ArrayList<>(types);
			for (int i = 0; i < typesCopy.size(); ++i) {
				String typeAsString = typesCopy.get(i);
				for (int j = 0; j < typeParameters.size(); ++j) {
					typeAsString = typeAsString.replaceAll(
							"\\b" + typeParameters.get(j).name.toString() + "\\b",
							"§_typeParam_" + j);
				}
				typesCopy.set(i, typeAsString);
			}

			return Arrays.toString(typesCopy.toArray());
		}

		@Override
		public void visitMethodDef(JCMethodDecl tree) {
			super.visitMethodDef(tree);

			if (classLevel != 1 || !isPublic) {
				return;
			}

			ArrayList<String> types = new ArrayList<>();
			for (JCVariableDecl decl : tree.params) {
				final Symbol paramTypeSymbol = TreeInfo.symbol(decl.vartype);
				String fullyQualifiedType = decl.vartype.toString();
				if (paramTypeSymbol != null) {
					fullyQualifiedType = paramTypeSymbol.toString();
					if (fullyQualifiedType.indexOf("cleanroom.") == 0) {
						fullyQualifiedType = fullyQualifiedType.substring("cleanroom.".length());
					}
				}
				types.add(fullyQualifiedType);
			}

			final List<JCTypeParameter> typeParams = typeParamStack.isEmpty()
					? tree.typarams
					: typeParamStack.peek().appendList(tree.typarams);
			String name = tree.name.toString() + ": " +  getTypesAsString(types, typeParams);
			if (isCleanroom) {
				cleanMethods.put(name, new Replacement(typeParamStack.peek(), tree));
			} else {
				if (cleanMethods.containsKey(name)) {
					if (isReplace(name)) {
						System.err.println("duplicate method: " + name + ", taken from cleanroom");

						final Replacement replacement = cleanMethods.get(name);
						result = new TypeParameterTranslator(replacement.typeParams, typeParams)
								.translate(replacement.tree);
					} else {
						System.err.println("duplicate method: " + name + ", taken from student");
					}
					// we use that method, so don't put it in later in
					cleanMethods.remove(name);
				}
			}
		}

		private List<JCTree> appendAll(List<JCTree> list, final List<JCTypeParameter> typeParams,
				final Map<String, Replacement> cleanObjects) {

			for (Map.Entry<String, Replacement> entry : cleanObjects.entrySet()) {
				final Replacement replacement = entry.getValue();
				list = list.append(new TypeParameterTranslator(replacement.typeParams, typeParams)
						.translate(replacement.tree));
			}
			return list;
		}

		@Override
		public void visitClassDef(JCClassDecl tree) {
			JCModifiers mods = tree.getModifiers();
			boolean oldPublic = isPublic;
			isPublic = mods.getFlags().contains(javax.lang.model.element.Modifier.PUBLIC);
			System.err.println("class " + tree.name + " ispub " + isPublic);
			if (isCleanroom && !isPublic && !tree.name.toString().startsWith(CLEAN_PREFIX)) {
				System.err.println("non-public class in cleanroom must be prefixed with " + CLEAN_PREFIX);
				System.exit(-1);
			}
			insideBlock = false;
			classLevel++;
			if (classLevel > 1) {
				System.err.println("found inner class: " + tree.name);
			}
			if (tree.getKind() == Kind.ENUM) {
				// remove default constructor from *inner* enums to make code valid
				List<JCTree> schlepp = null;
				for (List<JCTree> t = tree.defs; t.nonEmpty(); t = t.tail) {
					if (t.head != null && t.head.getKind() == Kind.METHOD) {
						JCTree.JCMethodDecl m = (JCTree.JCMethodDecl) t.head;
						if (m.name.toString().equals("<init>") && m.params.size() == 0) {
							if (schlepp == null) {
								tree.defs = t.tail;
							} else {
								schlepp.tail = t.tail;
							}
							break;
						}
					}
					schlepp = t;
				}
			}
			typeParamStack.push(tree.typarams);
			super.visitClassDef(tree);
			typeParamStack.pop();
			classLevel--;
			isPublic = oldPublic;

			System.err.println("class " + tree.name + ", " + isCleanroom + ", " + classLevel + ", " + isPublic);

			if (isCleanroom && classLevel >= 1 && tree.name.toString().startsWith(CLEAN_PREFIX)) {
				// remember additional inner classes of cleanroom
				// those will be added later in student's public class
				cleanInnerClasses.put(tree.name.toString(), new Replacement(tree.typarams, tree));
			}

			// only add methods, fields and inner classes in
			// outer
			// public class
			// of student
			if (classLevel >= 1 // no outer class
					|| !mods.getFlags().contains(javax.lang.model.element.Modifier.PUBLIC) // no public class
					|| isCleanroom) { // no student class
				return;
			}

			tree.defs = appendAll(tree.defs, tree.typarams, cleanMethods);
			tree.defs = appendAll(tree.defs, tree.typarams, cleanFields);
			tree.defs = appendAll(tree.defs, tree.typarams, cleanInnerClasses);
			
			result = tree;
		}
	}

	private class TypeParameterTranslator extends TreeTranslator {

		private final Map<Name, Name> typeParamMap = new HashMap<>();
		private boolean inType = false;

		TypeParameterTranslator(final List<JCTypeParameter> cleanroomTypeParams,
				final List<JCTypeParameter> currentTypeParams) {
			if (cleanroomTypeParams != null && currentTypeParams != null) {
				for (final Iterator<JCTypeParameter> cleanroomIt = cleanroomTypeParams.iterator(),
						currentIt = currentTypeParams.iterator();
						cleanroomIt.hasNext() && currentIt.hasNext();) {
					this.typeParamMap.put(cleanroomIt.next().name, currentIt.next().name);
				}
			}
		}

		@Override
		public <T extends JCTree> T translate(final T tree) {
			if (inType && tree instanceof JCIdent) {
				final JCIdent ident = (JCIdent) tree;
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
	}
}
