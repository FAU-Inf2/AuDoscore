import java.io.*;
import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.*;
import javax.lang.model.element.*;

import com.sun.source.tree.*;
import com.sun.source.util.*;
import com.sun.tools.javac.code.*;
import com.sun.tools.javac.model.*;
import com.sun.tools.javac.processing.*;
import com.sun.tools.javac.tree.*;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.util.*;

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
						java.util.List<? extends ImportTree> imports = path.getCompilationUnit().getImports();
						for (Object o : imports) {
							System.out.print(o);
						}
					}
					StringWriter stringWriter = new StringWriter();
					try {
						new MyPretty(stringWriter, false).printExpr(tree);
					} catch (IOException e) {
						throw new AssertionError(e);
					}
					System.out.println(stringWriter);
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
	private boolean inEnum = false;

	public MyPretty(Writer out, boolean sourceOutput) {
		super(out, sourceOutput);
	}

	@Override
	public void visitAnnotation(JCAnnotation tree) {
		try {
			print("@");
			String before = tree.annotationType.toString();
			switch (before) {
				case "After", "Before", "Ignore", "FixMethodOrder", "Rule", "ClassRule", "Test" -> print("org.junit.");
				case "RunWith" -> print("org.junit.runner.");
				case "Parameters" -> print("org.junit.runners.Parameterized.");
				case "CompareInterface", "Ex", "Exercises", "Forbidden", "InitializeOnce", "NotForbidden", "Points", "Replace", "SafeCallers", "SecretClass" -> print("tester.annotations.");
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

	@Override
	public void visitClassDef(final JCClassDecl tree) {
		final boolean oldInEnum = this.inEnum;
		this.inEnum = (tree.mods.flags & Flags.ENUM) > 0;
		super.visitClassDef(tree);
		this.inEnum = oldInEnum;
	}

	@Override
	public void visitApply(final JCMethodInvocation tree) {
		if (inEnum && tree.meth instanceof JCIdent ident) {
			if (ident.name == ident.name.table.names._super) {
				return;
			}
		}
		super.visitApply(tree);
	}
}
