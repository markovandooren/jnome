package jnome.core.type;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.association.OrderedMultiAssociation;

import chameleon.core.declaration.Declaration;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.type.IntersectionType;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.core.type.generics.ActualTypeArgument;
import chameleon.core.type.generics.TypeConstraintWithReferences;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.exception.ChameleonProgrammerException;

public class IntersectionTypeReference extends NamespaceElementImpl<JavaTypeReference,Element> implements JavaTypeReference {

	public IntersectionTypeReference() {
		
	}

	public IntersectionTypeReference(List<? extends JavaTypeReference> refs) {
		addAll(refs);
	}
	
	@Override
	public IntersectionTypeReference clone() {
		return new IntersectionTypeReference(typeReferences());
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

	public Type getType() throws LookupException {
		return getElement();
	}

	public Declaration getDeclarator() throws LookupException {
		throw new LookupException("Requesting declarator for an intersection type reference.");
	}

	public Type getElement() throws LookupException {
		List<Type> types = new ArrayList<Type>();
		for(TypeReference ref: typeReferences()) {
			types.add(ref.getElement());
		}
		return new IntersectionType(types);
	}

	public JavaTypeReference erasedReference() {
		return _types.getOtherEnds().get(0).erasedReference();
	}
	
	public void add(JavaTypeReference tref) {
		if(tref != null) {
			_types.add(tref.parentLink());
		}
	}
	
	public void addAll(List<? extends JavaTypeReference> refs) {
		for(JavaTypeReference ref: refs) {
			add(ref);
		}
	}
	
	public void remove(JavaTypeReference tref) {
		if(tref != null) {
			_types.remove(tref.parentLink());
		}
	}
	
	public List<JavaTypeReference> typeReferences() {
		return _types.getOtherEnds();
	}
	
	private OrderedMultiAssociation<IntersectionTypeReference,JavaTypeReference> _types = new OrderedMultiAssociation<IntersectionTypeReference, JavaTypeReference>(this);

	public List<Element> children() {
		return new ArrayList<Element>(typeReferences());
	} 


}
