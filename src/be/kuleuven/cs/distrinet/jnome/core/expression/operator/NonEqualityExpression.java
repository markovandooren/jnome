package be.kuleuven.cs.distrinet.jnome.core.expression.operator;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaView;

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
