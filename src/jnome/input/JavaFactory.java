package jnome.input;

import jnome.core.method.JavaNormalMethod;
import jnome.core.type.RegularJavaType;
import chameleon.aspect.oo.weave.factory.OOFactory;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.oo.expression.Expression;
import chameleon.oo.method.MethodHeader;
import chameleon.oo.plugin.ObjectOrientedFactory;
import chameleon.oo.statement.Statement;
import chameleon.oo.type.RegularType;
import chameleon.plugin.Plugin;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.support.statement.FinallyClause;
import chameleon.support.statement.ReturnStatement;
import chameleon.support.statement.TryStatement;

public class JavaFactory extends ObjectOrientedFactory implements OOFactory {
	
	public JavaFactory() {
		
	}
	
	public RegularType createRegularType(SimpleNameSignature signature) {
		return new RegularJavaType(signature);
	}
	
	public NormalMethod createNormalMethod(MethodHeader header) {
		return new JavaNormalMethod(header);
	}

	@Override
	public Plugin clone() {
		return new JavaFactory();
	}

	@Override
	public Statement createReturn(Expression expression) {
		return new ReturnStatement(expression);
	}

	@Override
	public Statement createTryFinally(Statement tr, Statement fin) {
		TryStatement result = new TryStatement(tr);
		result.setFinallyClause(new FinallyClause(fin));
		return result;
	}
}
