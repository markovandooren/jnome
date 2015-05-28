package be.kuleuven.cs.distrinet.jnome.core.expression;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.reference.CrossReferenceTarget;
import org.aikodi.chameleon.oo.expression.MethodInvocation;
import org.aikodi.chameleon.oo.expression.NameExpression;
import org.aikodi.chameleon.oo.type.Type;

import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeInstantiation;

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
		if(false) {
		  final Declaration declaration = nearestAncestor(Declaration.class);
		  if(method.hasAncestor(declaration)) 
		    if(result instanceof JavaTypeInstantiation) {
		      result = ((JavaTypeInstantiation) result).captureConversion();
		    }
		}
		return result;
	}
	
	private boolean nonInLeftHandSideOfAssignment() {
		return false;
	}
}
