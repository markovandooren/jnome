package be.kuleuven.cs.distrinet.jnome.core.type;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.VerificationResult;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.IntersectionTypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ActualTypeArgument;

public class JavaIntersectionTypeReference extends IntersectionTypeReference implements JavaTypeReference {
	
	public JavaIntersectionTypeReference() {
		
	}

	public JavaIntersectionTypeReference(List<? extends TypeReference> refs) {
		addAll(refs);
	}
	
	@Override
	public JavaIntersectionTypeReference clone() {
		List<TypeReference> trefs = new ArrayList<TypeReference>();
		for(TypeReference tref: typeReferences()) {
			trefs.add(tref.clone());
		}
		return new JavaIntersectionTypeReference(trefs);
	}

	@Override
	public VerificationResult verifySelf() {
    return Valid.create();
	}

	public void addAllArguments(List<ActualTypeArgument> arguments) {
		throw new ChameleonProgrammerException("Cannot add arguments to an intersection type reference");
	}

	public void addArgument(ActualTypeArgument argument) {
		throw new ChameleonProgrammerException("Cannot add an argument to an intersection type reference");
	}

	public void addArrayDimension(int i) {
		throw new ChameleonProgrammerException("Cannot change the dimension of an intersection type reference");
	}

	public int arrayDimension() {
		return 0;
	}

	public JavaTypeReference toArray(int dimension) {
		throw new ChameleonProgrammerException("Cannot change the dimension of an intersection type reference");
	}

	public List<ActualTypeArgument> typeArguments() {
		return new ArrayList<ActualTypeArgument>();
	}

	public JavaTypeReference erasedReference() {
		return ((JavaTypeReference)elementAt(1)).erasedReference();
	}

	public JavaTypeReference componentTypeReference() {
		return this;
	}
	

}
