package jnome.core.type;

import java.util.List;

import chameleon.oo.type.DerivedType;
import chameleon.oo.type.ParameterSubstitution;
import chameleon.oo.type.Type;
import chameleon.oo.type.generics.ActualTypeArgument;


public class CapturedType extends DerivedType {

	public CapturedType(ParameterSubstitution substitution, Type baseType) {
		super(substitution, baseType);
	}

	public CapturedType(Type baseType, List<ActualTypeArgument> typeParameters) {
		super(baseType, typeParameters);
	}
	
	public CapturedType(List<ParameterSubstitution> parameters, Type baseType) {
		super(parameters, baseType);
	}

	@Override
	public CapturedType clone() {
		return new CapturedType(clonedParameters(),baseType());
	}

}
