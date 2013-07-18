package be.kuleuven.cs.distrinet.jnome.core.type;

import java.util.Collections;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Verification;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.UnionTypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ActualTypeArgument;

public class JavaUnionTypeReference extends UnionTypeReference implements JavaTypeReference {
	
	public JavaUnionTypeReference() {
		
	}

	public JavaUnionTypeReference(List<? extends TypeReference> refs) {
		addAll(refs);
	}
	
	@Override
	public JavaUnionTypeReference cloneSelf() {
		return new JavaUnionTypeReference(Collections.EMPTY_LIST);
	}

	@Override
	public Verification verifySelf() {
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
		return Collections.EMPTY_LIST;
	}

	public JavaTypeReference erasedReference() {
		return ((JavaTypeReference)elementAt(1)).erasedReference();
	}

	public JavaTypeReference componentTypeReference() {
		return this;
	}
	

}
