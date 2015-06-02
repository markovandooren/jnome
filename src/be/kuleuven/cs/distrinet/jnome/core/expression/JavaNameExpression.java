package be.kuleuven.cs.distrinet.jnome.core.expression;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.reference.CrossReferenceTarget;
import org.aikodi.chameleon.oo.expression.MethodInvocation;
import org.aikodi.chameleon.oo.expression.NameExpression;
import org.aikodi.chameleon.oo.type.Type;

import be.kuleuven.cs.distrinet.jnome.core.type.JavaType;

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
		final MethodInvocation method = nearestAncestor(MethodInvocation.class);
		if(method != null) { // does not work yet because of bug in Java7.reference(Type) with multiple constraints
		  final Declaration declaration = nearestAncestor(Declaration.class);
		  if(method.hasAncestor(declaration)) 
		  	result = ((JavaType) result).captureConversion();
		}
		return result;
	}
	
	private boolean nonInLeftHandSideOfAssignment() {
		return false;
	}
}
