package be.kuleuven.cs.distrinet.jnome.core.expression;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.reference.CrossReferenceTarget;
import org.aikodi.chameleon.oo.expression.MethodInvocation;
import org.aikodi.chameleon.oo.expression.NameExpression;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.util.Util;

import be.kuleuven.cs.distrinet.jnome.core.type.JavaType;

public class JavaNameExpression extends NameExpression {

	public JavaNameExpression(String identifier, CrossReferenceTarget target) {
		super(identifier, target);
	}

	public JavaNameExpression(String identifier) {
		this(identifier,null);
	}
	
	@Override
	public NameExpression cloneSelf() {
		return new JavaNameExpression(name());
	}

	@Override
	protected Type actualType() throws LookupException {
		Type result = super.actualType();
    // JLS7 : If the expression name appears in a context where it is subject to assignment
    // conversion or method invocation conversion or casting conversion, then the type
    // of the expression name is the declared type of the field, local variable, or parameter
    // after capture conversion (§5.1.10).
		final MethodInvocation method = nearestAncestor(MethodInvocation.class);
		if(method != null) {
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
