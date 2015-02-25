package be.kuleuven.cs.distrinet.jnome.core.expression.operator;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.oo.type.Type;

import be.kuleuven.cs.distrinet.jnome.workspace.JavaView;

public class EqualityExpression extends OperatorExpression {

	public EqualityExpression(Expression first, Expression second) {
		super(first,"==",second);
	}

	@Override
	protected Type actualType() throws LookupException {
		return view(JavaView.class).primitiveType("boolean");
	}

	@Override
	protected EqualityExpression cloneSelf() {
		return new EqualityExpression(null,null);
	}

}
