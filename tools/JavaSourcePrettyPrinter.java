package tools;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.*;
import java.io.IOException;
import java.io.Writer;

public class JavaSourcePrettyPrinter extends com.sun.tools.javac.tree.Pretty {
	private boolean inEnum = false;

	public JavaSourcePrettyPrinter(Writer out, boolean sourceOutput) {
		super(out, sourceOutput);
	}

	@Override
	public void visitClassDef(final JCClassDecl classDeclaration) {
		final boolean oldInEnum = this.inEnum;
		this.inEnum = (classDeclaration.mods.flags & Flags.ENUM) > 0;
		boolean isRecord = (classDeclaration.mods.flags & Flags.RECORD) != 0;
		if (isRecord) { // FIXME: hacky workaround to get (static) records pretty printed as records instead of classes
			boolean first;
			try {
				println();
				super.visitModifiers(classDeclaration.mods);
				print("record " + classDeclaration.name);
				if (classDeclaration.typarams.nonEmpty()) {
					print("<");
					print(classDeclaration.typarams);
					print(">");
				}
				StringBuilder recordConstructorParameterTypes = new StringBuilder();
				first = true;
				print("(");
				for (JCTree member : classDeclaration.defs) {
					if (member instanceof JCVariableDecl variableDeclaration && (variableDeclaration.mods.flags & Flags.GENERATED_MEMBER) != 0) {
						if (!first) {
							print(", ");
							recordConstructorParameterTypes.append(",");
						}
						first = false;
						super.printAnnotations(variableDeclaration.mods.annotations);
						print(variableDeclaration.vartype + " " + variableDeclaration.name);
						recordConstructorParameterTypes.append(variableDeclaration.vartype);
					}
				}
				print(")");
				if (classDeclaration.implementing != null && classDeclaration.implementing.nonEmpty()) { // records can implement, but never extend
					print(" implements " + classDeclaration.implementing);
				}
				print(" {");
				println();
				for (JCTree member : classDeclaration.defs) {
					if (member instanceof JCVariableDecl variableDeclaration && (variableDeclaration.mods.flags & Flags.GENERATED_MEMBER) == 0) {
						super.visitVarDef(variableDeclaration);
					} else if (member instanceof JCMethodDecl methodDeclaration && (methodDeclaration.mods.flags & Flags.GENERATED_MEMBER) == 0) {
						if (methodDeclaration.getReturnType() != null) { // regular method
							super.visitMethodDef(methodDeclaration);
						} else if (!methodDeclaration.sym.toString().equals(classDeclaration.name + "(" + recordConstructorParameterTypes + ")")) { // not the main record constructor
							// FIXME: if the main record constructor is explicitly implemented, it will be removed here (the PrettyPrinter doesn't see it at all?)!
							String s = methodDeclaration.toString().replace("<init>", classDeclaration.name);
							print(s);
						}
					} else { // FIXME: missing anything else here?
						print("// missing something? skipped: " + member.getTag() + " / " + member.getKind());
					}
					println();
				}
				print("}");
			} catch (IOException e) {
				throw new Error("something failed while pretty printing: " + e);
			}
		} else {
			super.visitClassDef(classDeclaration);
		}
		this.inEnum = oldInEnum;
	}

	@Override
	public void visitApply(final JCMethodInvocation methodInvocation) {
		if (inEnum && methodInvocation.meth instanceof JCIdent ident) {
			if (ident.name == ident.name.table.names._super) {
				return;
			}
		}
		super.visitApply(methodInvocation);
	}

	@Override
	public void visitVarDef(com.sun.tools.javac.tree.JCTree.JCVariableDecl variableDeclaration) {
		// FIXME: hacky workaround to get rid of "/*missing*/" printed instead of "var"
		if (variableDeclaration.isImplicitlyTyped()) {
			variableDeclaration.vartype = new com.sun.tools.javac.tree.JCTree.JCExpression() {
				@Override
				public com.sun.tools.javac.tree.JCTree.Tag getTag() {
					return null;
				}

				@Override
				public void accept(com.sun.tools.javac.tree.JCTree.Visitor visitor) {
					if (visitor instanceof com.sun.tools.javac.tree.Pretty prettyPrinter) {
						try {
							prettyPrinter.print("var");
						} catch (java.io.IOException e) {
							throw new Error("something failed while pretty printing: " + e);
						}
					}
				}

				@Override
				public com.sun.source.tree.Tree.Kind getKind() {
					return null;
				}

				@Override
				public <R, D> R accept(com.sun.source.tree.TreeVisitor<R, D> treeVisitor, D d) {
					return null;
				}
			};
		}
		super.visitVarDef(variableDeclaration);
	}

	@Override
	public void visitPatternCaseLabel(JCTree.JCPatternCaseLabel patternCaseLabel) {
		// FIXME: hacky workaround to get rid of "/*missing*/" printed instead of "var"
		try {
			String s = patternCaseLabel.toString();
			if (s.contains("/*missing*/")) {
				print(s.replace("/*missing*/", "var"));
			} else {
				super.visitPatternCaseLabel(patternCaseLabel);
			}
		} catch (IOException e) {
			throw new Error("something failed while pretty printing: " + e);
		}
	}

	@Override
	public void visitYield(JCYield tree) {
		// FIXME: hacky workaround to get rid of "yield" printed without block parenthesis "{yield ...;}"
		try {
			print("{");
			super.visitYield(tree);
			print("}");
		} catch (IOException e) {
			throw new Error("something failed while pretty printing: " + e);
		}
	}
}
