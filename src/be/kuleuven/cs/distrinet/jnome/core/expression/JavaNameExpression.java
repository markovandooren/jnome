package be.kuleuven.cs.distrinet.jnome.core.expression;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.reference.CrossReferenceTarget;
import org.aikodi.chameleon.oo.expression.NameExpression;
import org.aikodi.chameleon.oo.type.Type;

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
