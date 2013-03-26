package be.kuleuven.cs.distrinet.jnome.core.type;

import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.DerivedType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.ParameterSubstitution;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ActualTypeArgument;


public class CapturedType extends JavaDerivedType {

	public CapturedType(ParameterSubstitution substitution, Type baseType) {
		super(substitution, baseType);
	}

	public CapturedType(Type baseType, List<ActualTypeArgument> typeParameters) throws LookupException {
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
