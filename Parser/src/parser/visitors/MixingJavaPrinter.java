package parser.visitors;
import parser.bast.nodes.*;
import parser.bast.type.*;
import parser.odin.*;
import java.util.*;

public class MixingJavaPrinter extends EnlightenedJavaPrinter {
	// FIXME: this is very, very dirty, please fix this
	public boolean first = true;
	public ArrayList<String> keep;
	public String superClass;
	public boolean inMethod = false;

	public MixingJavaPrinter(String superClass, ArrayList<String> keep) {
		this.superClass = superClass;
		this.keep = keep;
	}

	@Override
	public void visit(BastClassDecl node) {
		if (node.modifiers != null) {
			for (AbstractBastSpecifier modifier: node.modifiers) {
				if (modifier instanceof BastTypeQualifier) {
					BastTypeQualifier m = (BastTypeQualifier) modifier;
					if (m.type == BastTypeQualifier.TYPE_PUBLIC) {
						// TODO: looks ugly, ask Georg for a better way
						String newName = " " + superClass;
						for (String s : keep) {
							newName += "_" + s;
						}
						TokenAndHistory tah[] = {new TokenAndHistory(new JavaToken(BasicJavaToken.IDENTIFIER, newName))};

						node.name = new BastNameIdent(tah, newName);
						tah = new TokenAndHistory[]{new TokenAndHistory(new JavaToken(BasicJavaToken.IDENTIFIER, " " + superClass + " "))};
						node.extendedClass = new BastClassType(tah, new BastNameIdent(null, " " + superClass + " "), null, null);
						super.visit(node);
						// FIXME: this assumes no relevant inner or sibling classes!
						// all non-public classes are thrown away
					}
				}
			}
		}
	}

	@Override
	public void visit(BastFunction node) {
		inMethod = true;
		boolean thisFirst = first;
		first = false;

		if (node.decl == null) { // can this happen?
			super.visit(node);
		} else {
			boolean isConstructor = (node.returnType == null);

			if (isConstructor) {
				// FIXME: better idea: modified statement list, but we need Georg to do this
				if (node.modifiers!=null){
					for (AbstractBastSpecifier spec: node.modifiers){
						spec.accept(this);
					}
				}

				if (node.typeParameters!=null) {
					for (BastTypeParameter param : node.typeParameters) {
						param.accept(this);
					}
				}

				if (node.specifierList!=null){
					for (AbstractBastSpecifier spec: node.specifierList){
						spec.accept(this);
					}
				}
				if (node.returnType!=null){
					node.returnType.accept(this);
				}

				lastIdentifiers.clear();
				if (node.decl!=null){
					node.decl.accept(this);
				}
				if (node.exceptions != null) {
					addTokenData(node, 0);
					for (AbstractBastExpr expr : node.exceptions) {
						expr.accept(this);
					}
				}

				buffer.append(" {\n");
				buffer.append("\t\tsuper(");
				for (int i = 1; i < lastIdentifiers.size(); i++) {
					if (i > 1) {
						buffer.append(", ");
					}
					buffer.append(lastIdentifiers.get(i));
				}
				buffer.append(");\n");
				buffer.append("\t}\n");
			} else {
				lastIdentifiers.clear();
				StringBuffer tmpBuf = buffer; // write method signature in new buffer, save old buffer for a while
				buffer = new StringBuffer();
				node.decl.accept(this);
				buffer = tmpBuf; // use original buffer again

				String meth = lastIdentifiers.size() > 0 ? lastIdentifiers.get(0) : "INTERNAL_ERROR";
				if (keep.contains(meth)) {
					super.visit(node);
				} else if (thisFirst) {
					buffer.append(" {\n");
				}
			}
		}
		inMethod = false;
	}

	@Override
	public void visit(BastDeclaration node) {
		lastIdentifiers.clear();
		StringBuffer tmpBuf = buffer; // write decl in new buffer, save old buffer for a while
		buffer = new StringBuffer();
		if (node.declaratorList != null){
			for (AbstractBastDeclarator decl : node.declaratorList) {
				decl.accept(this);
			}
		}
		buffer = tmpBuf; // use original buffer again
		if (inMethod || (lastIdentifiers.size() > 0 && lastIdentifiers.get(0).startsWith("__clean"))) {
			super.visit(node);
		}
	}

	public ArrayList<String> lastIdentifiers = new ArrayList<>();
	@Override
	synchronized public void visit(BastIdentDeclarator node){
		if (node.identifier != null && node.identifier instanceof BastNameIdent) {
				BastNameIdent id = (BastNameIdent) node.identifier;
				lastIdentifiers.add(id.getName());
		}
		super.visit(node);
	}
}
