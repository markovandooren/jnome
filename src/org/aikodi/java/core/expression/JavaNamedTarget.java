package org.aikodi.java.core.expression;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.reference.CrossReferenceTarget;
import org.aikodi.chameleon.oo.expression.ExpressionFactory;
import org.aikodi.chameleon.oo.expression.MethodInvocation;
import org.aikodi.chameleon.oo.expression.NamedTarget;
import org.aikodi.chameleon.oo.type.DeclarationWithType;
import org.aikodi.java.core.type.JavaType;

public class JavaNamedTarget extends NamedTarget {

	public JavaNamedTarget(String identifier, CrossReferenceTarget target) {
		super(identifier, target);
	}

	public JavaNamedTarget(String fullyQualifiedName, ExpressionFactory factory) {
		super(fullyQualifiedName, factory);
	}

	@Override
	public Declaration getElement() throws LookupException {
		Declaration result = super.getElement();
		if(result instanceof DeclarationWithType) {
			result = ((DeclarationWithType)result).declarationType();
			final MethodInvocation method = lexical().nearestAncestor(MethodInvocation.class);
			if(method != null) { // FIXME TODO Check this: does not work yet because of bug in Java7.reference(Type) with multiple constraints
				final Declaration declaration = lexical().nearestAncestor(Declaration.class);
				if(method.lexical().hasAncestor(declaration)) 
						result = ((JavaType) result).captureConversion();
			}
		}
		return result;
	}

	@Override
	protected JavaNamedTarget cloneSelf() {
	  return new JavaNamedTarget(name(), (CrossReferenceTarget)null);
	}
}
