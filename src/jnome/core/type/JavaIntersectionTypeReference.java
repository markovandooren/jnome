package jnome.core.type;

import java.util.ArrayList;
import java.util.List;

import chameleon.core.lookup.LookupException;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.type.IntersectionTypeReference;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.generics.ActualTypeArgument;
import chameleon.util.CreationStackTrace;

public class JavaIntersectionTypeReference extends IntersectionTypeReference<JavaIntersectionTypeReference> implements JavaTypeReference<JavaIntersectionTypeReference> {

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

//	public Type erasure() throws LookupException {
//		return erasedReference().erasure();
//	}

	public JavaTypeReference toArray(int dimension) {
		throw new ChameleonProgrammerException("Cannot change the dimension of an intersection type reference");
	}

	public List<ActualTypeArgument> typeArguments() {
		return new ArrayList<ActualTypeArgument>();
	}

	public JavaTypeReference erasedReference() {
		return ((JavaTypeReference)_types.getOtherEnds().get(0)).erasedReference();
	}

	public JavaTypeReference componentTypeReference() {
		return this;
	}
	

}
