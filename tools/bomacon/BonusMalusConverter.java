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
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;

@SupportedAnnotationTypes("*")
@SupportedOptions("replaces")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
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
	private JCAnnotation bonus = null;
	private JCAnnotation malus = null;
	public PointsPretty(Writer out, boolean sourceOutput) {
		super(out, sourceOutput);
	}

	private void printAnnotation(JCAnnotation tree) throws IOException{
			print("@");
			print("Points");
			print("(");
			printExprs(tree.args);
			print(")");
	}

	@Override
	public void visitMethodDef(JCMethodDecl tree){
		try{
			if(bonus != null && malus != null){
				// TODO merge the args ad Print Points
			}else if(bonus != null){
				printAnnotation(bonus);
			}else if(malus != null){
				printAnnotation(malus);
			}
		} catch(IOException e){
			throw new Error("something failed while converting BONUS/MALUS annotations to POINTS: " + e);
		}
		// reset
		bonus = null;
		malus = null;	
		super.visitMethodDef(tree);
	}
	
	@Override
	public  void visitAnnotation(JCAnnotation tree) {
		String annotation = tree.annotationType.toString();
		if(annotation.equals("Bonus") || annotation.equals("tester.annotations.Bonus")){
			bonus = tree;
		}else if(annotation.equals("Malus") || annotation.equals("tester.annotations.Malus")){
			malus = tree;
		}else {
			super.visitAnnotation(tree);
		}
	}
}
