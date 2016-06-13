import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
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
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;

@SupportedAnnotationTypes("*")
@SupportedOptions("replaces")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class FullQualifier extends AbstractProcessor {
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
							System.out.print(o);
						}
					}

					StringWriter s = new StringWriter();
					try {
						new MyPretty(s, false).printExpr(tree);
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

class MyPretty extends com.sun.tools.javac.tree.Pretty {
	public MyPretty(Writer out, boolean sourceOutput) {
		super(out, sourceOutput);
	}

	public  void visitAnnotation(JCAnnotation tree) {
		try {
			print("@");
			String before = tree.annotationType.toString();
			switch (before) {
				case "After":
				case "Before":
				case "Ignore":
				case "FixMethodOrder":
				case "Rule":
				case "ClassRule":
				case "Test":
					print("org.junit.");
					break;
				case "RunWith":
					print("org.junit.runner.");
					break;
				case "Parameters":
					print("org.junit.runners.Parameterized.");
					break;
				case "Bonus":
				case "Malus":
				case "Points":
				case "SecretCase":
				case "Ex":
				case "Exercises":
				case "Forbidden":
				case "NotForbidden":
				case "CompareInterface":
				case "Replace": 
				case "SecretClass":
					print("tester.annotations.");
					break;
			}
			printExpr(tree.annotationType);
			print("(");
			if (before.equals("RunWith") && tree.args.head.toString().equals("value = Parameterized.class")) {
				print("value = org.junit.runners.Parameterized.class");
			} else {
				printExprs(tree.args);
			}
			print(")");
		} catch (IOException e) {
			throw new Error("something failed while pretty printing annotations: " + e);
		}
	}
}
