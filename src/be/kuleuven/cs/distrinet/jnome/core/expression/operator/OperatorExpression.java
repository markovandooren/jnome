package be.kuleuven.cs.distrinet.jnome.core.expression.operator;

import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.support.expression.BinaryExpression;

public abstract class OperatorExpression extends BinaryExpression {

	public OperatorExpression(Expression first, String name, Expression second) {
		super(first,second);
		setName(name);
	}
	
	public String name() {
		return _name;
	}
	
	protected void setName(String name) {
		if(name == null) {
			throw new IllegalArgumentException();
		}
		_name = name;
	}

	private String _name;
}
