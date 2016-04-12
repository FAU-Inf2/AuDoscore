package tools.ptc;

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
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;

@SupportedAnnotationTypes("*")
@SupportedOptions("replaces")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
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

	Name enclClassName;
	public static final int ENUM = 1 << 14;
	public static final int INTERFACE = 1 << 9;


	public PrettyClean(Writer out, boolean sourceOutput) {
		super(out, sourceOutput);
	}


	@Override
	public void visitClassDef(JCClassDecl tree) {
		try {
			println();
			printDocComment(tree);
			printAnnotations(tree.mods.annotations);
			printFlags(tree.mods.flags & ~INTERFACE);
			Name enclClassNamePrev = enclClassName;
			enclClassName = tree.name;
			if ((tree.mods.flags & INTERFACE) != 0) {
				print("interface " + tree.name);
				printTypeParameters(tree.typarams);
				if (tree.implementing.nonEmpty()) {
					print(" extends ");
					printExprs(tree.implementing);
				}
			} else {
				if ((tree.mods.flags & ENUM) != 0)
					print("enum " + tree.name);
				else
					print("class " + tree.name);
				printTypeParameters(tree.typarams);
				if (tree.extending != null) {
					if(!tree.extending.toString().equals("JUnitWithPoints")){
						print(" extends ");
						printExpr(tree.extending);
					}
				}
				if (tree.implementing.nonEmpty()) {
					print(" implements ");
					printExprs(tree.implementing);
				}
			}
			print(" ");
			if ((tree.mods.flags & ENUM) != 0) {
				printEnumBody(tree.defs);
			} else {
				printBlock(tree.defs);
			}
			enclClassName = enclClassNamePrev;
		} catch (IOException e) {
			// FIXME: should be UncheckedIOException
			throw new Error("something failed while removing AuDoscore annotations: " + e);
		}
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
