package tools.ptc;

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
@SupportedSourceVersion(SourceVersion.RELEASE_21)
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

	private boolean importToBeSkipped(Object o) {
		return o.toString().contains("import tester.") || o.toString().contains("import org.junit.rules.");
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
							if (!importToBeSkipped(o)) {
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
					System.out.println(s);
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
	private boolean inEnum = false;

	public PrettyClean(Writer out, boolean sourceOutput) {
		super(out, sourceOutput);
	}

	@Override
	public void visitClassDef(JCClassDecl tree) {
		final boolean oldInEnum = this.inEnum;
		this.inEnum = (tree.mods.flags & Flags.ENUM) > 0;
		if (tree.extending != null && "JUnitWithPoints".equals(tree.extending.toString())) {
			// Remove JUnitWithPoints
			tree.extending = null;
		}
		if (tree.defs != null) {
			// resultDefinitions holds the resulting definitions
			final JCTree[] resultDefinitions = new JCTree[tree.defs.size()];
			int resultSize = 0;
			for (final JCTree def : tree.defs) {
				if (def instanceof final JCVariableDecl variableDeclaration) {
					if (variableDeclaration.vartype instanceof JCIdent) {
						final String typeName = ((JCIdent) variableDeclaration.vartype).name.toString();
						if ("PointsLogger".equals(typeName) || "PointsSummary".equals(typeName)) {
							// Remove variable declaration
							continue;
						}
					}
				}
				resultDefinitions[resultSize++] = def;
			}
			tree.defs = com.sun.tools.javac.util.List.from(Arrays.copyOf(resultDefinitions, resultSize));
		}
		super.visitClassDef(tree);
		this.inEnum = oldInEnum;
	}

	@Override
	public void visitAnnotation(JCAnnotation tree) {
		String before = tree.annotationType.toString();
		// FIXME: prefix??
		if (!before.equals("Test")) {
			// TODO handle @Rule and @ClassRule
			return;
		}
		try {
			print("@");
			printExpr(tree.annotationType);
			print("(");
			// if (before.equals("RunWith") && tree.args.head.toString().equals("value = Parameterized.class")) {
			// Dead code?
			//	print("value = org.junit.runners.Parameterized.class");
			// } else {
			printExprs(tree.args);
			// }
			print(")");
		} catch (IOException e) {
			throw new Error("something failed while removing AuDoscore annotations: " + e);
		}
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
