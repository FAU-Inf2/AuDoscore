package tools.bomacon;

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
import com.sun.tools.javac.tree.JCTree.JCModifiers;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

@SupportedAnnotationTypes("*")
@SupportedOptions("replaces")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class BonusMalusConverter extends AbstractProcessor {
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
						new PointsPretty(s, false).printExpr(tree);
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

class PointsPretty extends com.sun.tools.javac.tree.Pretty {
	public PointsPretty(Writer out, boolean sourceOutput) {
		super(out, sourceOutput);
	}

	private List<JCExpression> mergeAnnotations(JCAnnotation bonus, JCAnnotation malus) throws IOException {

		List<JCExpression> bonusArgs = bonus.args;
		List<JCExpression> malusArgs = malus.args;
		for (final JCExpression ex1 : malusArgs) {
			boolean equal = false;
			for (final JCExpression ex2 : bonusArgs) {
				if (ex1.toString().equals(ex2.toString())) {
					equal = true;
					break;
				}	
			}
			if (!equal) {
				bonusArgs = bonusArgs.append(ex1);
			}
		}
		return bonusArgs;	
	}

	private void printPoints(List<JCExpression> args) throws IOException {
		print("@");
		print("Points");
		print("(");
		printExprs(args);
		print(")");
	}

	public void visitModifiers(JCModifiers mods){
		try{
			JCAnnotation bonus = null;
			JCAnnotation malus = null;

			List<JCAnnotation> trees = mods.annotations;
			for (List<JCAnnotation> l = trees;l.nonEmpty(); l = l.tail) {
				JCAnnotation a = l.head;
				if (a.annotationType.toString().equals("Bonus")
						|| a.annotationType.toString().equals("tester.annotations.Bonus")) {
					bonus = a;
				} else if (a.annotationType.toString().equals("Malus")
						|| a.annotationType.toString().equals("tester.annotations.Malus")) {
					malus = a;
				} else {
					printStat(l.head);
					println();
				}
			}
			if (bonus != null && malus != null) {
				List<JCExpression> args = mergeAnnotations(bonus, malus);
				printPoints(args);
			} else if (bonus != null) {
				printPoints(bonus.args);
			} else if (malus != null) {
				printPoints(malus.args);
			}
			println();

			printFlags(mods.flags);
		} catch (IOException e) {
			throw new Error("something went wrong while converting BONUS/MALUS to POINTS annotation: " + e);
		}
	}
}
