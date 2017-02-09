package org.aikodi.java.core.expression.operator;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.java.workspace.JavaView;

public class NonEqualityExpression extends OperatorExpression {

	public NonEqualityExpression(Expression first, Expression second) {
		super(first,"!=",second);
	}

	@Override
	protected Type actualType() throws LookupException {
		return view(JavaView.class).primitiveType("boolean");
	}

	@Override
	protected NonEqualityExpression cloneSelf() {
		return new NonEqualityExpression(null,null);
	}

}
