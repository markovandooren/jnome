package jnome.core.type;

import java.util.ArrayList;
import java.util.List;

import chameleon.core.lookup.LookupException;
import chameleon.core.type.IntersectionTypeReference;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.core.type.generics.ActualTypeArgument;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.exception.ChameleonProgrammerException;

public class JavaIntersectionTypeReference extends IntersectionTypeReference<JavaIntersectionTypeReference> implements JavaTypeReference<JavaIntersectionTypeReference> {

	public JavaIntersectionTypeReference() {
		
	}

	public JavaIntersectionTypeReference(List<? extends TypeReference> refs) {
		addAll(refs);
	}
	
	@Override
	public JavaIntersectionTypeReference clone() {
		return new JavaIntersectionTypeReference(typeReferences());
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

	public Type erasure() throws LookupException {
		return erasedReference().erasure();
	}

	public void setArrayDimension(int i) {
		throw new ChameleonProgrammerException("Cannot change the dimension of an intersection type reference");
	}

	public JavaTypeReference toArray(int dimension) {
		throw new ChameleonProgrammerException("Cannot change the dimension of an intersection type reference");
	}

	public List<ActualTypeArgument> typeArguments() {
		return new ArrayList<ActualTypeArgument>();
	}

	public JavaTypeReference erasedReference() {
		return ((JavaTypeReference)_types.getOtherEnds().get(0)).erasedReference();
	}
	

}
