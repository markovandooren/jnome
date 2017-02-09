package org.aikodi.java.core.expression;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.support.expression.ClassCastExpression;
import org.aikodi.java.core.type.JavaType;

public class JavaClassCastExpression extends ClassCastExpression {

	public JavaClassCastExpression(TypeReference type, Expression expression) {
		super(type, expression);
	}

	@Override
	protected Type actualType() throws LookupException {
		Type result = ((JavaType)getTypeReference().getElement()).captureConversion();
		return result;
	}

}
