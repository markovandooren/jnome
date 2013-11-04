package be.kuleuven.cs.distrinet.jnome.core.expression;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReferenceTarget;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.NameExpression;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaDerivedType;

public class JavaNameExpression extends NameExpression {

	public JavaNameExpression(String identifier, CrossReferenceTarget target) {
		super(identifier, target);
	}

	public JavaNameExpression(String identifier) {
		super(identifier);
	}
	
	@Override
	public NameExpression cloneSelf() {
		return new JavaNameExpression(name());
	}

	@Override
	protected Type actualType() throws LookupException {
		Type result = super.actualType();
		if(nonInLeftHandSideOfAssignment()) {
			if(result instanceof JavaDerivedType) {
				result = ((JavaDerivedType) result).captureConversion();
			}
		}
		return result;
	}
	
	private boolean nonInLeftHandSideOfAssignment() {
		return false;
	}
}
