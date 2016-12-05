package tools.ptc;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Set;


import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import com.sun.source.util.Trees;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

@SupportedAnnotationTypes("*")
@SupportedOptions("replaces")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class PublicTestCleaner extends AbstractProcessor {
	private Trees trees;
	private boolean imported = false;

	@Override
	public synchronized void init(ProcessingEnvironment env) {
		super.init(env);
		trees = Trees.instance(env);
		Context context = ((JavacProcessingEnvironment) env).getContext();
		TreeMaker.instance(context);
		JavacElements.instance(context);
	}

	private boolean importToBeSkipped(Object o){
		if(o.toString().contains("import tester.")) {
			return true;
		}
		if(o.toString().contains("import org.junit.rules.")){
			return true;
		}
		if(o.toString().contains("import java.lang.reflect.")){
			return true;
		}
		return false;
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (!roundEnv.processingOver()) {
			Set<? extends Element> elements = roundEnv.getRootElements();
			for (Element each : elements) {
				if (each.getKind() == ElementKind.CLASS) {
					JCTree tree = (JCTree) trees.getTree(each);
					if (!imported) { 
						imported = true;
						TreePath path = trees.getPath(each);
						java.util.List imports = path.getCompilationUnit().getImports();
						for (Object o : imports) {
							if(!importToBeSkipped(o)){
								System.out.print(o);
							}
						}
					}

					StringWriter s = new StringWriter();
					try {
						new PrettyClean(s, false).printExpr(tree);
					} catch (IOException e) {
						throw new AssertionError(e);
					}
					System.out.println(s.toString());
				}
			}
			// even with -proc:only the javac does some semantic checking
			// to avoid that (we compile the generated files anyway in the next step), exit in a clean way
			System.exit(0);
		}
		return false;
	}
}

class PrettyClean extends com.sun.tools.javac.tree.Pretty {

	public PrettyClean(Writer out, boolean sourceOutput) {
		super(out, sourceOutput);
	}



	@Override
	public void visitClassDef(JCClassDecl tree) {
		if (tree.extending != null && "JUnitWithPoints".equals(tree.extending.toString())){
			// Remove JUnitWithPoints
			tree.extending = null;
		}
		if (tree.defs != null) {
			// resultDefs holds the resulting definitions
			final JCTree[] resultDefs = new JCTree[tree.defs.size()];
			int resultSize = 0;

			for (final JCTree def : tree.defs) {
				if (def instanceof JCVariableDecl) {
					final JCVariableDecl varDecl = (JCVariableDecl) def;
					if (varDecl.vartype instanceof JCIdent) {
						final String typeName = ((JCIdent) varDecl.vartype).name.toString();
						if ("PointsLogger".equals(typeName) || "PointsSummary".equals(typeName)) {
							// Remove variable decl
							continue;
						}
					}
				}
				resultDefs[resultSize++] = def;
			}

			tree.defs = List.from(Arrays.copyOf(resultDefs, resultSize));
		}
		super.visitClassDef(tree);
	}



	@Override
	public void visitAnnotation(JCAnnotation tree) {
		String before = tree.annotationType.toString();
		// FIXME: prefix??
		if(!before.equals("Test")){
			// TODO handle @Rule and @ClassRule
			return;
		}

		try {
			print("@");
			printExpr(tree.annotationType);
			print("(");
			if (before.equals("RunWith") && tree.args.head.toString().equals("value = Parameterized.class")) {
				// Dead code?
				print("value = org.junit.runners.Parameterized.class");
			} else {
				printExprs(tree.args);
			}
			print(")");
		} catch (IOException e) {
			throw new Error("something failed while removing AuDoscore annotations: " + e);
		}
	}
}
