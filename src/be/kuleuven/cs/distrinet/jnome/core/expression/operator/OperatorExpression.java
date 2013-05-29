package be.kuleuven.cs.distrinet.jnome.core.expression.operator;

import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.support.expression.BinaryExpression;

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
