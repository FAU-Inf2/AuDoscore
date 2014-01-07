import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import java.util.ArrayList;
import javax.annotation.processing.*;
import javax.lang.model.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.ElementKind;
import javax.tools.*;
import com.sun.source.util.Trees;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.*;
import com.sun.tools.javac.util.*;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.model.JavacElements;
import javax.lang.model.util.Elements;

@SupportedAnnotationTypes("*")
@SupportedOptions("replaces")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ReplaceMixer extends AbstractProcessor {
	private Trees trees;
	private TreeMaker make;
	private JavacElements elements;

	private HashMap<String, JCTree> cleanMethods = new HashMap<>();
	private HashMap<String, JCTree> cleanFields = new HashMap<>();
	private boolean first = true; // student vs. cleanroom
	private boolean isPublic = true;
	private int classLevel;

	public String[] replaces = null;

	@Override
	public synchronized void init(ProcessingEnvironment env) {
		super.init(env);
		trees = Trees.instance(env);
		Context context = ((JavacProcessingEnvironment) env).getContext();
		make = TreeMaker.instance(context);
		elements = JavacElements.instance(context);
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
				if (each.getKind() == ElementKind.CLASS) {
					classLevel = 0;
					JCTree tree = (JCTree) trees.getTree(each);
					tree.accept(new Merger());

					TreePath path = trees.getPath(each);
					java.util.List imports = path.getCompilationUnit().getImports();
					for (Object o : imports) {
						System.out.println(o);
					}

					if (!first) {
						System.out.println(tree);
					}
					first = false;
				}
			}
		}
		return false;
	}

	private boolean isReplace(String method) {
		if (replaces == null) return false;
		for (String s : replaces) {
			if (method.startsWith(s)) return true;
		}
		return false;
	}

	private class Merger extends TreeTranslator {
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

			if (insideBlock) return;

			String name = tree.name.toString();
			if (first && name.startsWith("__clean")) {
				cleanFields.put(name, tree);
			}
		}

		@Override
		public void visitMethodDef(JCMethodDecl tree) {
			super.visitMethodDef(tree);

			if (classLevel != 1) return;
			if (!isPublic) return;

			ArrayList<String> types = new ArrayList<>();
			for (JCVariableDecl decl : tree.params) {
				types.add(decl.vartype.toString());
			}
			String name = tree.name.toString() + ": " +  Arrays.toString(types.toArray());
			if (first) {
				cleanMethods.put(name, tree);
			} else {
				if (cleanMethods.get(name) != null) {
					if (isReplace(name)) {
						System.err.println("duplicate method: " + name + ", taken from cleanroom");
						result = cleanMethods.get(name);
					} else {
						System.err.println("duplicate method: " + name + ", taken from student");
					}
					// we use that method, so don't put it in later in
					cleanMethods.remove(name);
				}
			}
		}

		@Override
		public void visitClassDef(JCClassDecl tree) {
			JCModifiers mods = tree.getModifiers();
			boolean oldPublic = isPublic;
			isPublic = mods.getFlags().contains(javax.lang.model.element.Modifier.PUBLIC);
			System.err.println("class " + tree.name + " ispub " + isPublic);
			insideBlock = false;
			classLevel++;
			if (classLevel > 1) {
				System.err.println("found inner class: " + tree.name);
			}
			super.visitClassDef(tree);
			classLevel--;
			isPublic = oldPublic;

			System.err.println("class " + tree.name + ", " + first + ", " + classLevel + ", " + isPublic);

			if (first) return;
			if (classLevel >= 1) return;
			if (!mods.getFlags().contains(javax.lang.model.element.Modifier.PUBLIC)) return;

			System.err.println("I am here");

			for (Map.Entry<String, JCTree> entry : cleanMethods.entrySet()) {
				System.err.println("appending cleanroom method: " + entry.getKey());
				tree.defs = tree.defs.append(entry.getValue());
			}

			for (Map.Entry<String, JCTree> entry : cleanFields.entrySet()) {
				System.err.println("appending cleanroom field: " + entry.getKey());
				tree.defs = tree.defs.append(entry.getValue());
			}

			
			result = tree;
		}
	}
}
