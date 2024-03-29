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
	public void visitVarDef(JCVariableDecl variableDeclaration) {
		// FIXME: hacky workaround to get rid of "/*missing*/" printed instead of "var"
		if (variableDeclaration.declaredUsingVar()) {
			try {
				super.printDocComment(variableDeclaration);
				super.printAnnotations(variableDeclaration.mods.annotations);
				super.visitModifiers(variableDeclaration.mods);
				print("var ");
				print(variableDeclaration.name);
				if (variableDeclaration.init != null) {
					print(" = ");
					print(variableDeclaration.init);
					print(";");
				}
			} catch (IOException e) {
				throw new Error("something failed while pretty printing: " + e);
			}
		} else {
			super.visitVarDef(variableDeclaration);
		}
	}

	@Override
	public void visitForLoop(JCForLoop forLoop) {
		// FIXME: hacky workaround to get rid of "/*missing*/" printed instead of "var"
		try {
			print("for (");
			boolean first = true;
			for (JCStatement statement : forLoop.init) {
				if (!first) print(", ");
				if (statement instanceof JCVariableDecl variableDeclaration) {
					if (first) {
						if (variableDeclaration.declaredUsingVar()) {
							print("var");
						} else {
							print(variableDeclaration.getType());
						}
						print(" ");
					}
					print(variableDeclaration.getName());
					if (variableDeclaration.init != null) {
						print(" = ");
						print(variableDeclaration.init);
					}
				} else {
					print(((JCExpressionStatement) statement).expr);
				}
				first = false;
			}
			print("; ");
			if (forLoop.cond != null) print(forLoop.cond);
			print("; ");
			first = true;
			for (JCExpressionStatement statement : forLoop.step) {
				if (!first) print(", ");
				first = false;
				print(statement.expr);
			}
			print(") ");
			if (forLoop.body instanceof JCAssert body) super.visitAssert(body);
			else if (forLoop.body instanceof JCBlock body) super.visitBlock(body);
			else if (forLoop.body instanceof JCBreak body) super.visitBreak(body);
			else if (forLoop.body instanceof JCCase body) super.visitCase(body);
			else if (forLoop.body instanceof JCClassDecl body) super.visitClassDef(body);
			else if (forLoop.body instanceof JCContinue body) super.visitContinue(body);
			else if (forLoop.body instanceof JCDoWhileLoop body) super.visitDoLoop(body);
			else if (forLoop.body instanceof JCEnhancedForLoop body) super.visitForeachLoop(body);
			else if (forLoop.body instanceof JCExpressionStatement body) super.visitExec(body);
			else if (forLoop.body instanceof JCForLoop body) super.visitForLoop(body);
			else if (forLoop.body instanceof JCIf body) super.visitIf(body);
			else if (forLoop.body instanceof JCLabeledStatement body) super.visitLabelled(body);
			else if (forLoop.body instanceof JCReturn body) super.visitReturn(body);
			else if (forLoop.body instanceof JCSkip body) super.visitSkip(body);
			else if (forLoop.body instanceof JCSwitch body) super.visitSwitch(body);
			else if (forLoop.body instanceof JCSynchronized body) super.visitSynchronized(body);
			else if (forLoop.body instanceof JCThrow body) super.visitThrow(body);
			else if (forLoop.body instanceof JCTry body) super.visitTry(body);
			else if (forLoop.body instanceof JCVariableDecl body) super.visitVarDef(body);
			else if (forLoop.body instanceof JCWhileLoop body) super.visitWhileLoop(body);
			else if (forLoop.body instanceof JCYield body) super.visitYield(body);
			else print(forLoop.body); // FIXME: missing anything else here? try best effort if so...
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
