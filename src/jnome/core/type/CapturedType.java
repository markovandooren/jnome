package jnome.core.type;

import java.util.List;

import chameleon.oo.type.DerivedType;
import chameleon.oo.type.Type;
import chameleon.oo.type.generics.ActualTypeArgument;
import chameleon.oo.type.generics.TypeParameter;


public class CapturedType extends DerivedType {

	public CapturedType(List<TypeParameter> typeParameters, Type baseType) {
		super(TypeParameter.class,typeParameters, baseType);
	}

	public CapturedType(Type baseType, List<ActualTypeArgument> typeParameters) {
		super(baseType, typeParameters);
	}

}
